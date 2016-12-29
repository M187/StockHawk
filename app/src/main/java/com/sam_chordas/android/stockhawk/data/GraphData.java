package com.sam_chordas.android.stockhawk.data;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Created by michal.hornak on 12/29/2016.
 */
public class GraphData {

    public ArrayList<Entry> responseCloseNodeValues = new ArrayList<>();
    private float leftAxisMax = 0;
    private float leftAxisMin = 0;

    public float getLeftAxisMax() {
        return leftAxisMax;
    }

    public float getLeftAxisMin() {
        return leftAxisMin;
    }

    public float getLeftAxisMaxForGraph() {
        return leftAxisMax + 2f;
    }

    public void setLeftAxisMax(float leftAxisMax) {
        this.leftAxisMax = leftAxisMax;
    }

    public float getLeftAxisMinForGraph() {
        return leftAxisMin - 2f;
    }

    public void setLeftAxisMin(float leftAxisMin) {
        this.leftAxisMin = leftAxisMin;
    }
}
