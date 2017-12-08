package com.qiqu.yuqing.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * Created by YJS on 2017-06-03.
 */

public class QiquUtil {

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getShortUrl( String longUrl ){
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL( "http://api.t.sina.com.cn/short_url/shorten.json?source=3271760578&url_long="+ URLEncoder.encode(longUrl));
            // 创建连接
            conn =  (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);
            conn.connect();
            InputStream inStream = conn.getInputStream();

            StringBuffer data = new StringBuffer();
            byte[] buffer = new byte[1024];
            Arrays.fill( buffer, (byte)0 );
            int read;
            while( (read = inStream.read(buffer)) != -1){
                byte[] tmp = new byte[read];
                System.arraycopy( buffer,1,tmp,0,read-2);
                data.append( new String(tmp) );
            }
            ShortUrlData sud = JSON.parseObject( data.toString(), ShortUrlData.class );
            conn.disconnect();
            return  sud.getUrl_short();
        } catch ( Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            if( conn != null )
                conn.disconnect();
        }
        return null;
    }
    public static byte[] bmpToByteArray(final Bitmap bitmap, final boolean needRecycle,int maxkb ) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb&& options != 10) {
            output.reset(); //清空output
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);//这里压缩options%，把压缩后的数据存放到output中
            options -= 10;
        }
        return output.toByteArray();
    }
}
