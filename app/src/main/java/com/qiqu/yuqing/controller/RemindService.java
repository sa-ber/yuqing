package com.qiqu.yuqing.controller;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.qiqu.yuqing.MainActivity;
import com.qiqu.yuqing.view.QiquWebView;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.concurrent.ScheduledExecutorService;


/**
 * Created by YJS on 2017-06-01.
 */

public class RemindService extends Service {

    private ScheduledExecutorService mSes;
    private RemindBinder mBinder;

    private CheckTask mTask;
    private SharedPreferences mPrefs;

    public static final int PAGESIZE = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new RemindBinder();
        mPrefs = getSharedPreferences( MainActivity.PREFS, MODE_PRIVATE);
        Log.i(MainActivity.TAG, "service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i( MainActivity.TAG, "service destroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class RemindBinder extends Binder{

        @TargetApi(Build.VERSION_CODES.N)
        public void checkNews(QiquWebView webView ){
            boolean flag = mPrefs.getBoolean("remind",false);
            initPrefs();
            Log.i(MainActivity.TAG,"flag="+flag);
            if( flag ){
                int period = mPrefs.getInt("peroid", 5);
                mTask = new CheckTask(webView, period*1000*60 );
                webView.postDelayed( mTask, 5000 );
            }
        }
        public void restartCheck( QiquWebView webView ){
            if( webView != null )
                webView.removeCallbacks( mTask );
            boolean flag = mPrefs.getBoolean("remind",false);
            if( flag ){
                int period = mPrefs.getInt("peroid", 5);
                mTask = new CheckTask(webView, period*1000*60 );
                webView.postDelayed( mTask, 5000 );
            }

        }


        private void initPrefs(){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format( new Date() );
            String recordDate = mPrefs.getString("date","");
            if( !date.equals(recordDate) ){
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putString("date", date);
            }
        }
    }

    private class CheckTask implements Runnable{
        private QiquWebView mWebView;
        private int mDelay;

        public CheckTask( QiquWebView webView,int delay ){
            mWebView = webView;
            mDelay = delay;
        }
        @Override
        public void run() {
            mWebView.loadUrl("javascript:queryWarnList(1,"+PAGESIZE+")");
            mWebView.postDelayed( this , mDelay );
        }
    }
}
