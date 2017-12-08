package com.qiqu.yuqing.model;

/**
 * Created by YJS on 2017-06-11.
 */

public class ShortUrlData {
    private String url_short;
    private String url_long;
    private int type;

    public String getUrl_short() {
        return url_short;
    }

    public void setUrl_short(String url_short) {
        this.url_short = url_short;
    }

    public String getUrl_long() {
        return url_long;
    }

    public void setUrl_long(String url_long) {
        this.url_long = url_long;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
