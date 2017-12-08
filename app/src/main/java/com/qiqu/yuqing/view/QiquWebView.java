package com.qiqu.yuqing.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qiqu.yuqing.MainActivity;

/**
 * Created by YJS on 2017-05-20.
 */

public class QiquWebView extends WebView {
    private Context mContext;
    public QiquWebView(Context context ) {
        super( context );
        init();
    }
    public QiquWebView(Context context, AttributeSet attrs ) {
        super(context, attrs );
        init();
    }
    public QiquWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        mContext = getContext();
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
        WebSettings mWebSettings = this.getSettings();
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY );
        setScrollbarFadingEnabled(false);
        setVerticalScrollBarEnabled(false);
        requestFocus();
        mWebSettings.setTextSize(WebSettings.TextSize.NORMAL );

        mWebSettings.setCacheMode( WebSettings.LOAD_NO_CACHE );
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls( true );
        mWebSettings.setDomStorageEnabled( true );
        setWebViewClient(mWebViewClient);
    }

    private WebViewClient mWebViewClient = new WebViewClient(){

        public boolean shouldOverrideUrlLoading(WebView view, String strUrl)
        {
            if ( strUrl.startsWith("tel:") )
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));
                mContext.startActivity(intent);
                return true;
            }
            else
                view.loadUrl(strUrl);
            return false;
        }
        public void onPageStarted(WebView view, String strUrl, Bitmap favicon)
        {
            super.onPageStarted(view, strUrl, favicon);
        }
        public void onPageFinished(WebView view, String strUrl)
        {
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.i( MainActivity.TAG, "error="+error);
            view.loadUrl("file:///android_asset/error.html");
        }
    };

}
