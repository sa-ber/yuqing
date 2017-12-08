package com.qiqu.yuqing.controller;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qiqu.yuqing.MainActivity;


/**
 * Created by YJS on 2017-06-03.
 */

public class NotificationReceiver extends BroadcastReceiver {
    public static final String TYPE = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id",-1);
        if( id != -1 ){
            NotificationManager manager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
            manager.cancel(id);
        }
        String url = intent.getStringExtra("url");
        Log.i(MainActivity.TAG, "url="+url );
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("url", url );
        i.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(i);
    }
}
