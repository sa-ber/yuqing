package com.qiqu.yuqing.model;

import java.util.List;

/**
 * Created by YJS on 2017-06-03.
 */

public class News {
    private Msg msg;
    private Data data;

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public String text;
    public int code;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public class Data {
        private int currentPageNo;
        private int currentPageSize;
        private boolean empty;
        private int totalCount;
        private int totalPageCount;
        private List<Article> dataList;

        public int getCurrentPageNo() {
            return currentPageNo;
        }

        public void setCurrentPageNo(int currentPageNo) {
            this.currentPageNo = currentPageNo;
        }

        public int getCurrentPageSize() {
            return currentPageSize;
        }

        public void setCurrentPageSize(int currentPageSize) {
            this.currentPageSize = currentPageSize;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty(boolean empty) {
            this.empty = empty;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public int getTotalPageCount() {
            return totalPageCount;
        }

        public void setTotalPageCount(int totalPageCount) {
            this.totalPageCount = totalPageCount;
        }

        public List<Article> getDataList() {
            return dataList;
        }

        public void setDataList(List<Article> dataList) {
            this.dataList = dataList;
        }
    }
    public class Msg {
        public String text;
        public int code;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
