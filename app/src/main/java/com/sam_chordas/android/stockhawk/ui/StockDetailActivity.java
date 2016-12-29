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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.GraphData;
import com.sam_chordas.android.stockhawk.volley.AppRequestQueue;
import com.sam_chordas.android.stockhawk.volley.FetchPricesOverTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by michal.hornak on 11/5/2016.
 */
public class StockDetailActivity extends FragmentActivity implements SeekBar.OnSeekBarChangeListener {

    private LineChart mChart;

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

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDrawGridBackground(true);

        // no description text
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText(getResources().getString(R.string.graph_description_string));

        // set an alternative background color
        mChart.setBackgroundColor(Color.GRAY);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        //llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        // don't forget to refresh the drawing
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

    public void setData(GraphData graphData){
        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(graphData.responseCloseNodeValues);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(graphData.responseCloseNodeValues, "DataSet 1");

            // set the line to be drawn like this "- - - - - -"
            set1.setColor(Color.BLACK);
            set1.setValueTextSize(9f);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
            setLeftAxisSize(graphData.getLeftAxisMaxForGraph(), graphData.getLeftAxisMinForGraph());
            mChart.invalidate();
        }
    }

    private void setLeftAxisSize(float max, float min){
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLimitLinesBehindData(true);
    }
}