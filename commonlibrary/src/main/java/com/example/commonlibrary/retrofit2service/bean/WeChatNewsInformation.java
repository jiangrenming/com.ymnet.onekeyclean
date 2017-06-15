package com.example.commonlibrary.retrofit2service.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MajinBuu on 2017/6/14 0014.
 *
 * @overView ${todo}.
 */

public class WeChatNewsInformation implements Serializable {

    /**
     * code : 200
     * msg : 请求成功
     * data : {"result":[{"id":"18554","title":"揭地下卵子交易：美女一颗5万 90万定制男宝宝","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/b7f77995c534806f9867bbcc1c665964.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10152.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/b7f77995c534806f9867bbcc1c665964.jpg"},{"id":"18555","title":"2女1男为逃票翻墙进动物园 落地时周围站了7只虎","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/88f39a59090b7e9d4985e5a6d4deb15b.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10154.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/88f39a59090b7e9d4985e5a6d4deb15b.jpg"},{"id":"18556","title":"带娃上班不慎落在车内 女婴死亡母亲被判杀人","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/673d77465ee63b7d77310ebdcfd4b591.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10144.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/673d77465ee63b7d77310ebdcfd4b591.jpg"},{"id":"18557","title":"49岁护工厌倦疗养院工作 7年杀死8位老年人","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/25e919743cee55c3e200f2072416bf58.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10145.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/25e919743cee55c3e200f2072416bf58.jpg"},{"id":"18558","title":"29岁女儿为2块拿刀刺伤69岁父亲 肇事者已被控制","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/375fbcea7812ecaabf4860a45d33158a.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10147.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/375fbcea7812ecaabf4860a45d33158a.jpg"},{"id":"18559","title":"被吻过的地方全过敏 杭州美女怀疑男友口水有毒","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/adaf81580622851619b65ddd81c0b05e.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10148.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/adaf81580622851619b65ddd81c0b05e.jpg"},{"id":"18560","title":"被二次碾压女子身份曝光 父母至今未看监控视频","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/9ea280a63fc070c8966b94d15143e9ae.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10150.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/9ea280a63fc070c8966b94d15143e9ae.jpg"}],"page":33,"count":"228","open_ad":"on"}
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
         * result : [{"id":"18554","title":"揭地下卵子交易：美女一颗5万 90万定制男宝宝","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/b7f77995c534806f9867bbcc1c665964.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10152.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/b7f77995c534806f9867bbcc1c665964.jpg"},{"id":"18555","title":"2女1男为逃票翻墙进动物园 落地时周围站了7只虎","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/88f39a59090b7e9d4985e5a6d4deb15b.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10154.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/88f39a59090b7e9d4985e5a6d4deb15b.jpg"},{"id":"18556","title":"带娃上班不慎落在车内 女婴死亡母亲被判杀人","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/673d77465ee63b7d77310ebdcfd4b591.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10144.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/673d77465ee63b7d77310ebdcfd4b591.jpg"},{"id":"18557","title":"49岁护工厌倦疗养院工作 7年杀死8位老年人","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/25e919743cee55c3e200f2072416bf58.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10145.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/25e919743cee55c3e200f2072416bf58.jpg"},{"id":"18558","title":"29岁女儿为2块拿刀刺伤69岁父亲 肇事者已被控制","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/375fbcea7812ecaabf4860a45d33158a.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10147.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/375fbcea7812ecaabf4860a45d33158a.jpg"},{"id":"18559","title":"被吻过的地方全过敏 杭州美女怀疑男友口水有毒","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/adaf81580622851619b65ddd81c0b05e.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10148.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/adaf81580622851619b65ddd81c0b05e.jpg"},{"id":"18560","title":"被二次碾压女子身份曝光 父母至今未看监控视频","author_name":"","pics":"[\"http://i.jun4.com/d/file/2017-06-12/9ea280a63fc070c8966b94d15143e9ae.jpg\"]","news_url":"http://4g.yigouu.com/04/shehui/20170612/10150.html","type":"all","show_type":"3","publish_time":"06-13 10:51","source":"","thumbnail_pic_s1":"http://i.jun4.com/d/file/2017-06-12/9ea280a63fc070c8966b94d15143e9ae.jpg"}]
         * page : 33
         * count : 228
         * open_ad : on
         */

        private int page;
        private int           count;
        private String           open_ad;
        private List<ResultBean> result;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getOpen_ad() {
            return open_ad;
        }

        public void setOpen_ad(String open_ad) {
            this.open_ad = open_ad;
        }

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * id : 18554
             * title : 揭地下卵子交易：美女一颗5万 90万定制男宝宝
             * author_name :
             * pics : ["http://i.jun4.com/d/file/2017-06-12/b7f77995c534806f9867bbcc1c665964.jpg"]
             * news_url : http://4g.yigouu.com/04/shehui/20170612/10152.html
             * type : all
             * show_type : 3
             * publish_time : 06-13 10:51
             * source :
             * thumbnail_pic_s1 : http://i.jun4.com/d/file/2017-06-12/b7f77995c534806f9867bbcc1c665964.jpg
             */

            private int id;
            private String title;
            private String author_name;
            private String pics;
            private String news_url;
            private String type;
            private int show_type;
            private String publish_time;
            private String source;
            private String thumbnail_pic_s1;
            private String thumbnail_pic_s2;
            private String thumbnail_pic_s3;

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

            public String getAuthor_name() {
                return author_name;
            }

            public void setAuthor_name(String author_name) {
                this.author_name = author_name;
            }

            public String getPics() {
                return pics;
            }

            public void setPics(String pics) {
                this.pics = pics;
            }

            public String getNews_url() {
                return news_url;
            }

            public void setNews_url(String news_url) {
                this.news_url = news_url;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getShow_type() {
                return show_type;
            }

            public void setShow_type(int show_type) {
                this.show_type = show_type;
            }

            public String getPublish_time() {
                return publish_time;
            }

            public void setPublish_time(String publish_time) {
                this.publish_time = publish_time;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getThumbnail_pic_s1() {
                return thumbnail_pic_s1;
            }

            public void setThumbnail_pic_s1(String thumbnail_pic_s1) {
                this.thumbnail_pic_s1 = thumbnail_pic_s1;
            }

            public String getThumbnail_pic_s2() {
                return thumbnail_pic_s2;
            }

            public void setThumbnail_pic_s2(String thumbnail_pic_s2) {
                this.thumbnail_pic_s2 = thumbnail_pic_s2;
            }

            public String getThumbnail_pic_s3() {
                return thumbnail_pic_s3;
            }

            public void setThumbnail_pic_s3(String thumbnail_pic_s3) {
                this.thumbnail_pic_s3 = thumbnail_pic_s3;
            }
        }
    }
}
