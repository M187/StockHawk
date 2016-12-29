package com.sam_chordas.android.stockhawk.volley;

import com.android.volley.Response;
import com.github.mikephil.charting.data.BarEntry;
import com.sam_chordas.android.stockhawk.data.GraphData;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by michal.hornak on 12/28/2016.
 *
 * Response listener to parse incoming JSON object containing prices of a stock value over time.
 * As a part of processing it is initializing data into graph of a prent Activity.
 */
public class FetchPricesOverTime implements Response.Listener<JSONObject> {

    //public ArrayList<BarEntry> responseCloseNodeValues;
    public GraphData graphData;
    private StockDetailActivity parent;

    public FetchPricesOverTime(StockDetailActivity stockDetailActivity){
        this.parent = stockDetailActivity;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            graphData = new GraphData();
            //responseCloseNodeValues = new ArrayList<>();
            parseResponse(
                    response
                            .getJSONObject("query")
                            .getJSONObject("results")
                            .getJSONArray("quote"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.parent.setData(graphData);
    }

    private void parseResponse(JSONArray jsonArray) {
        int i = 0;
        float currentValue;
        while (i < jsonArray.length()) {
            try {

                currentValue = (float) Double.parseDouble(jsonArray.getJSONObject(i).get("Close").toString());

                if (graphData.getLeftAxisMax() < currentValue || i == 0) {graphData.setLeftAxisMax(currentValue);}
                if (graphData.getLeftAxisMin() > currentValue || i == 0) {graphData.setLeftAxisMin(currentValue);}

                graphData.responseCloseNodeValues.add(new BarEntry((float) i, currentValue));

            } catch (Exception e) {
            }
            i++;
        }
    }
}
