package com.qiqu.yuqing;

import android.app.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.os.IBinder;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qiqu.yuqing.controller.NotificationReceiver;
import com.qiqu.yuqing.controller.RemindService;
import com.qiqu.yuqing.controller.UpdateTools;
import com.qiqu.yuqing.model.Article;
import com.qiqu.yuqing.model.DealFileClass;
import com.qiqu.yuqing.model.News;
import com.qiqu.yuqing.model.QiquUtil;
import com.qiqu.yuqing.view.QiquPopupWindow;
import com.qiqu.yuqing.view.QiquWebView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ezy.boost.update.UpdateUtil;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = "yuqing";
    //首页url
   public final static String BASEURL = "http://yqb.yingyan189.com/";
  // public final static String BASEURL = "http://27.155.97.86:8004/pobe/";
    public final static String HOMEURL =  BASEURL+"r/m/sys/toLoginPage";
    public final static String NOTIFITIONURL = BASEURL+"r/m/news/getNewsDetail";
    //配置文件目录
   // public final static String CONFIGURL = HOMEURL+"/apk/version.xml";
    public final static int SHARE_CODE = 1001;
    private QiquWebView mQiquWebView;
    private long mExitTime = 0;
    private UpdateTools mUpdate;
    public final static String PREFS = "Qiqu";
    private SharedPreferences mPref;
    public static int mVersionCode;

    private static final String APP_ID = "wxf7e621b9838333e1";
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQiquWebView = (QiquWebView) findViewById(R.id.webView);
        mQiquWebView.addJavascriptInterface(this, "AndroidWebView");
        //加载首页
        mQiquWebView.loadUrl(HOMEURL);

        mVersionCode = QiquUtil.getVersionCode(this);
        mUpdate = new UpdateTools(this);
        mUpdate.checkUpdate(false);

        mPref = getSharedPreferences(PREFS, MODE_PRIVATE);

        String scheme = getIntent().getScheme();
        Uri uri = getIntent().getData();
        Log.i(TAG, "scheme:" + scheme);
        if (uri != null) {
            String url = uri.getQueryParameter("url");
            url = URLDecoder.decode(url);
            Log.i(TAG, "queryString:" + url);
            mQiquWebView.loadUrl(url);
        }

        //注册到微信
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        String url = getIntent().getStringExtra("url");
        Log.i(TAG, "url="+url);
        if( url != null && url != "") {
            mQiquWebView.loadUrl("javascript:clearStorage(\"warn\")");
            mQiquWebView.loadUrl(url);
        }
        else
        {
            String scheme = getIntent().getScheme();
            Uri uri = getIntent().getData();
            Log.i( TAG, "scheme:"+scheme);
            if (uri != null) {
                url = uri.getQueryParameter("url");
                url = URLDecoder.decode(url);
                mQiquWebView.loadUrl( url );
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId())
        {
            case R.id.action_update:
                break;
            case R.id.action_about:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ( keyCode == KeyEvent.KEYCODE_BACK )
        {
            if( mQiquWebView.canGoBack() )
                mQiquWebView.goBack();
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    public void shareOther( String title, String url )
    {
        String shortUrl = QiquUtil.getShortUrl(url);
        if( shortUrl == null  )
            shortUrl = url;
        String text = "#"+getString(R.string.app_name)+"#\n"+title+"\n链接地址："+shortUrl;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text );
        shareIntent.setType("text/plain");
        //设置分享列表的标题，并且每次都显示分享列表
        startActivityForResult( Intent.createChooser(shareIntent, "分享到"), SHARE_CODE );
    }
    private static final int THUMB_SIZE = 150;
    public void shareWeiXin( String title, String url,int scene,String digest )
    {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = digest;
        Bitmap bmp = BitmapFactory.decodeResource( getResources(), R.mipmap.shaer_logo );
        Bitmap thumb = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = QiquUtil.bmpToByteArray( thumb, true,20 );
        thumb.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;
        api.sendReq( req );

    }
    @JavascriptInterface
    public void startBrower( String url )
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse( url );
        intent.setData(content_url);
        startActivity(intent);
    }
    QiquPopupWindow popup;
    @JavascriptInterface
    public void Share(final String title, final String url )
    {
        Log.i( TAG, "url="+url);
       popup = new QiquPopupWindow(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( v.getId() == R.id.btn_wechat_session )
                {
                    shareWeiXin(title,url, SendMessageToWX.Req.WXSceneSession,"");
                }
                else if( v.getId() == R.id.btn_wechat_timeline )
                {
                    shareWeiXin(title,url, SendMessageToWX.Req.WXSceneTimeline,"" );
                }
                else
                {
                    shareOther( title, url );
                }
                popup.dismiss();
            }
        });
        popup.showAtLocation(MainActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    @JavascriptInterface
    public void Share(final String title, final String url ,final String digest )
    {
        Log.i( TAG, "url="+url);
        popup = new QiquPopupWindow(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( v.getId() == R.id.btn_wechat_session )
                {
                    shareWeiXin(title,url, SendMessageToWX.Req.WXSceneSession ,digest );
                }
                else if( v.getId() == R.id.btn_wechat_timeline )
                {
                    shareWeiXin(title,url, SendMessageToWX.Req.WXSceneTimeline, digest );
                }
                else
                {
                    shareOther( title, url );
                }
                popup.dismiss();
            }
        });
        popup.showAtLocation(MainActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    @JavascriptInterface
    public void cleanCache()
    {
        UpdateUtil.clean( this );

        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");
        Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());

        if(webviewCacheDir.exists()){
            DealFileClass.cleanDirectory(getCacheDir().getAbsolutePath()+"/webviewCache");
        }
    }
    @JavascriptInterface
    public void checkUpdate()
    {
        mUpdate.checkUpdate();
    }
    @JavascriptInterface
    public String getVersion()
    {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if( info != null)
            return  info.versionName;
        return "";
    }
    @JavascriptInterface
    public int getFirstLauncher()
    {

        int isFirstLauncher = mPref.getInt("isFirst", 1);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("isFirst", 0);
        editor.commit();
        return isFirstLauncher;
    }
    @JavascriptInterface
    public void setNotificationFlag( boolean remindFlag, boolean soundFlag, boolean vibrateFlag, int period ){

        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("sound", soundFlag );
        editor.putBoolean("vibrate", vibrateFlag );
        editor.putInt("period", period );
        editor.putBoolean( "remind", remindFlag );
        editor.commit();
        Log.i(TAG,"sound="+soundFlag+" vibrate="+vibrateFlag+" period="+period+" remind="+remindFlag );
        if( mBinder != null )
            mBinder.restartCheck(mQiquWebView);
    }
    @JavascriptInterface
    public String getNotificationFlag(){

        int period = mPref.getInt("period",0);
        boolean sound = mPref.getBoolean("sound",false );
        boolean vibrate = mPref.getBoolean("vibrate", false );
        boolean remind = mPref.getBoolean("remind", false);
        String json = "{\"period\":"+period+",\"sound\":"+sound+",\"vibrate\":"+vibrate+",\"remind\":"+remind+"}";
        return json;
    }
    private RemindService.RemindBinder mBinder = null;
    private ServiceConnection mConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "onServiceConnected");
            mBinder = (RemindService.RemindBinder) iBinder;
            mBinder.checkNews(mQiquWebView);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @JavascriptInterface
    public void sendWarnList( String json )
    {
        if( json == null || json == "" )
        {
            return;
        }
        Log.i( TAG,"json="+json);

        SharedPreferences.Editor edit = mPref.edit();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format( new Date() );
        String recordDate = mPref.getString("date","");
        News news = JSON.parseObject( json, News.class );
        if( !date.equals(recordDate) ){

            edit.putString("date", date);
            edit.putInt("total", 0 );
            edit.commit();
        }
        int total = mPref.getInt("total",0);
        News.Data data = news.getData();
        List<Article>newsList = data.getDataList();

        if( total  < data.getTotalCount() || compareMsgDate(newsList) ){
            edit.putInt("total",data.getTotalCount() );
            if( data.getDataList() != null && data.getDataList().size() > 0 ){
                sendNotification( newsList.get(0).getTitle(),
                        newsList.get(0).getId());
                edit.putString("lastDate", newsList.get(0).getPublishTime() );
            }
            edit.commit();
        }

    }
    @JavascriptInterface
    public void startService(){
        Log.i(TAG, "startService");
        Intent service = new Intent( this,RemindService.class);
       bindService( service, mConnect, BIND_AUTO_CREATE );
    }

    private void sendNotification( String title, String newsId ) {

        int id = (int)System.currentTimeMillis()/1000;
        String url = NOTIFITIONURL+"?type=1&id="+newsId;
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent clickIntent = new Intent( this, NotificationReceiver.class );
        clickIntent.putExtra("id", id );
        clickIntent.putExtra("url",  url );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( this,0, clickIntent, PendingIntent.FLAG_ONE_SHOT );
        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.logo_icon)
                    .setContentTitle("预警信息")
                    .setContentText(title)
                    .setContentIntent( pendingIntent );
        int flag = Notification.DEFAULT_LIGHTS;

        if( mPref.getBoolean("sound",false) )
            flag |= Notification.DEFAULT_SOUND;
        if( mPref.getBoolean("vibrate", false) )
            flag |= Notification.DEFAULT_VIBRATE;
        builder.setDefaults( flag );
        notifyManager.notify( id, builder.build());
    }


    private boolean compareMsgDate(List<Article> dataList ) {
        if( dataList == null || dataList.size() <= 0 )
            return false;

        String lastDate = mPref.getString("lastDate","");
        if( lastDate == "" )
            return true;
        String date = dataList.get(0).getPublishTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            Date d1 = sdf.parse( lastDate );
            Date d2 = sdf.parse( date );
            return d2.after(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
