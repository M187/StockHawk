package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.volley.AppRequestQueue;
import com.sam_chordas.android.stockhawk.volley.FetchPricesOverTime;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by michal.hornak on 11/5/2016.
 */
public class StockDetailActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener {

    private BarChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    private ArrayList<Entry> dataOverTime = new ArrayList<>();
    private FetchPricesOverTime fetcher = new FetchPricesOverTime(this);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail_layout);

        Bundle bundle = getIntent().getExtras();

        ((TextView) findViewById(R.id.detail_stock_symbol)).setText(getResources().getString(R.string.stock_symbol_string) + " " + bundle.getString("Symbol"));
        ((TextView) findViewById(R.id.detail_bid_price)).setText(getResources().getString(R.string.bid_price_string) +  " " + bundle.getString("BidPrice"));
        ((TextView) findViewById(R.id.detail_percent_change)).setText(getResources().getString(R.string.percent_change_string) +  " " + bundle.getString("PercentChange"));
        ((TextView) findViewById(R.id.detail_change)).setText(getResources().getString(R.string.change_string) +  " " + bundle.getString("Change"));

        fetchStockOverTimeData(bundle);
        createLineGraph();
    }


    private void fetchStockOverTimeData(Bundle bundle){


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        String finishDate = dateFormat.format(cal.getTime());

        cal.add(Calendar.DATE, -10);
        String startDate = dateFormat.format(cal.getTime());

        String url = ("http://query.yahooapis.com/v1/public/yql?q=select%20Close%20from%20yahoo.finance.historicaldata%20" +
                "where%20symbol%20in%20%28%27" + bundle.getString("Symbol") + "%27%29%20" +
                "and%20startDate%20=%20%27" + startDate + "%27%20" +
                "and%20endDate%20=%20%27" + finishDate + "%27&format=json&diagnostics=true&env=store://datatables.org/alltableswithkeys");

        AppRequestQueue.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        this.fetcher,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getBaseContext(), "Error during fetching data!", Toast.LENGTH_LONG).show();
                                Log.d("ToastError:", error.toString());
                            }
                        }
                ));
    }


    private void createLineGraph(){

        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);
        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
//        mSeekBarX.setProgress(45);
//        mSeekBarY.setProgress(100);

//        mSeekBarY.setOnSeekBarChangeListener(this);
//        mSeekBarX.setOnSeekBarChangeListener(this);

        mChart = (BarChart) findViewById(R.id.chart1);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum(40f);
        leftAxis.setAxisMinimum(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> data = new ArrayList<>();
        data.add(new BarEntry(1,31));
        data.add(new BarEntry(2,35));
        data.add(new BarEntry(3,33));
        data.add(new BarEntry(4,36));
        data.add(new BarEntry(5,33));
        setData(data);

        //mChart.animateX(2500);
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        ArrayList<Entry> data = new ArrayList<>();
        data.add(new Entry(1,1));
        data.add(new Entry(2,5));
        data.add(new Entry(3,3));
        data.add(new Entry(4,6));
        data.add(new Entry(5, -3));
        setData(data);

        // redraw
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    public void setData(ArrayList dataList){
        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(dataList);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new BarDataSet(dataList, "DataSet 1");

            // set the line to be drawn like this "- - - - - -"
            set1.setColor(Color.BLACK);
            set1.setValueTextSize(9f);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            //set1.setFillColor(Color.WHITE);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            BarData data = new BarData(dataSets);

            // set data
            mChart.setData(data);
            mChart.invalidate();
        }
    }
}