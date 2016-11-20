package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

/**
 * Created by michal.hornak on 11/5/2016.
 */
public class StockDetailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail_layout);

        Bundle bundle = getIntent().getExtras();

        ((TextView) findViewById(R.id.detail_stock_symbol)).setText("Stock symbol: " + bundle.getString("Symbol"));
        ((TextView) findViewById(R.id.detail_bid_price)).setText("Bid price: " + bundle.getString("BidPrice"));
        ((TextView) findViewById(R.id.detail_percent_change)).setText("Percent change: " + bundle.getString("PercentChange"));
        ((TextView) findViewById(R.id.detail_change)).setText("Change: " +bundle.getString("Change"));

    }

}
