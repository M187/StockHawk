package com.sam_chordas.android.stockhawk.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by michal.hornak on 12/28/2016.
 */
public class AppRequestQueue {

    private static AppRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private AppRequestQueue(Context context){
        this.mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized AppRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}