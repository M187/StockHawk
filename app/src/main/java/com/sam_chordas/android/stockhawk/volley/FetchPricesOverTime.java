package com.sam_chordas.android.stockhawk.volley;

import com.android.volley.Response;
import com.github.mikephil.charting.data.BarEntry;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by michal.hornak on 12/28/2016.
 *
 * Response listener to parse incoming JSON object containing prices of a stock value over time.
 * As a part of processing it is initializing data into graph of a prent Activity.
 */
public class FetchPricesOverTime implements Response.Listener<JSONObject> {

    public ArrayList<BarEntry> responseCloseNodeValues;
    private StockDetailActivity parent;

    public FetchPricesOverTime(StockDetailActivity stockDetailActivity){
        this.parent = stockDetailActivity;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            responseCloseNodeValues = new ArrayList<>();
            parseResponse(
                    response
                            .getJSONObject("query")
                            .getJSONObject("results")
                            .getJSONArray("quote"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.parent.setData(responseCloseNodeValues);
    }

    private void parseResponse(JSONArray jsonArray) {
        int i = 0;
        while (i < jsonArray.length()) {
            try {
                responseCloseNodeValues.add(new BarEntry((float)i, (float)Double.parseDouble(jsonArray.getJSONObject(i).get("Close").toString())));
            } catch (Exception e) {
            }
            i++;
        }
    }
}
