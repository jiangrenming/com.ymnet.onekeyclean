package com.ymnet.killbackground.model.bean;

import java.util.List;

/**
 * Created by MajinBuu on 2017/6/26 0026.
 *
 * @overView 清理引导界面信息bean
 */

public class CleanEntrance {
    /**
     * code : 200
     * data : [{"icon":"http://i.jun4.com/d/file/2017-06-26/4ae8ac72b53a59fa8483545f23738bfb.jpg","id":1,"title":"测试1","type":1,"url":"http://4g.yigouu.com/04/yule/20170626/14981.html"},{"icon":"","id":2,"title":"测试2","type":2,"url":""},{"icon":"http://i.jun4.com/d/file/2017-06-26/4ae8ac72b53a59fa8483545f23738bfb.jpg","id":3,"title":"测试3","type":1,"url":"http://4g.yigouu.com/04/yule/20170626/14981.html"},{"icon":"","id":4,"title":"测试4","type":2,"url":""},{"icon":"http://i.jun4.com/d/file/2017-06-26/4ae8ac72b53a59fa8483545f23738bfb.jpg","id":5,"title":"测试5","type":1,"url":"http://4g.yigouu.com/04/yule/20170626/14981.html"}]
     * msg : 请求成功
     */

    private int code;
    private String         msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * icon : http://i.jun4.com/d/file/2017-06-26/4ae8ac72b53a59fa8483545f23738bfb.jpg
         * id : 1
         * title : 测试1
         * type : 1
         * url : http://4g.yigouu.com/04/yule/20170626/14981.html
         */

        private String icon;
        private int    id;
        private String title;
        private int    type;
        private String url;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "icon='" + icon + '\'' +
                    ", id=" + id +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CleanEntrance{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
