package com.qiqu.yuqing.controller;

/**
 * Created by YJS on 2017-05-21.
 */

import android.content.Context;

import com.qiqu.yuqing.model.UpdateParser;

import ezy.boost.update.UpdateManager;

public class UpdateTools {
    //上下文对象
    private Context mContext;
    private String mCheckUrl = "http://fire.just77.cn/app/apk/yuqing.xml";
    public UpdateTools(Context context) {
        super();
        this.mContext = context;
       // UpdateManager.setDebuggable(true);
        UpdateManager.setWifiOnly( false );
    }
    /**
     * 检测软件更新
     */
    public void checkUpdate()
    {
        UpdateManager.create( mContext ).setUrl(mCheckUrl).setManual(true).setParser(new UpdateParser()).check();
    }

    public void checkUpdate(boolean flag ){
        UpdateManager.create( mContext ).setUrl(mCheckUrl).setManual(flag).setParser(new UpdateParser()).check();
    }
}
