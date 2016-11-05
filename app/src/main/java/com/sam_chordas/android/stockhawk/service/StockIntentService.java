package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  Handler mHandler;

  public StockIntentService(){
    super(StockIntentService.class.getName());
    this.mHandler = new Handler();
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
      args.putString("symbol", intent.getStringExtra("symbol"));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    try {
      stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    } catch (Exception e){
      this.mHandler.post(new DisplayToast(
              "Error while getting data for " + args.getString("symbol"), this));
    }
  }

  private class DisplayToast implements Runnable{
    String mText;
    IntentService intent;

    public DisplayToast(String text, IntentService intent){
      mText = text;
      this.intent = intent;
    }

    public void run(){
      Toast.makeText(this.intent, mText, Toast.LENGTH_SHORT).show();
    }
  }
}
