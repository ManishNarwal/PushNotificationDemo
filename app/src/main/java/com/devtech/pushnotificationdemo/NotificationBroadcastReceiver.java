package com.devtech.pushnotificationdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = NotificationBroadcastReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null && intent.getAction()!=null && intent.getAction().equals("notification")){
            Log.e(TAG,intent.getData().toString());
        }
    }
}
