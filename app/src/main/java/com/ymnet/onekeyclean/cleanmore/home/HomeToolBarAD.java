package com.ymnet.onekeyclean.cleanmore.home;

/**
 * Created by MajinBuu on 2017/7/7 0007.
 *
 * @overView ${todo}.
 */

public class HomeToolBarAD {
    /**
     * code : 200
     * msg : 请求成功
     * data : {"icon":"http://download.youmeng.com/zm_static/img/float_ad/red.gif","url":"http://m.bianxianmao.com?appKey=1eeb543e6f6b4c61bc7479c86914136f&appType=app&appEntrance=2&business=money&i=","key":"bianxianmao","open_ad":"on"}
     */

    private int code;
    private String   msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * icon : http://download.youmeng.com/zm_static/img/float_ad/red.gif
         * url : http://m.bianxianmao.com?appKey=1eeb543e6f6b4c61bc7479c86914136f&appType=app&appEntrance=2&business=money&i=
         * key : bianxianmao
         * open_ad : on
         */

        private String icon;
        private String url;
        private String key;
        private String open_ad;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getOpen_ad() {
            return open_ad;
        }

        public void setOpen_ad(String open_ad) {
            this.open_ad = open_ad;
        }
    }
}
