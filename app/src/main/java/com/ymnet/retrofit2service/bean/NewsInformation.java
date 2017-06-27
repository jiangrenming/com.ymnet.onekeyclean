package com.ymnet.retrofit2service.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jrm on 2017-5-2.
 * 信息流数据
 */

public class NewsInformation implements Serializable{


    /**
     * code : 200
     * msg : 请求成功
     * data : [{"id":"2482999","title":"男子因感情纠纷持刀劫持前妻 民警赴现场解救人质","author_name":"澎湃新闻","pics":"[\"http://08.imgmini.eastday.com/mobile/20170602/20170602113050_4a19f2bae05f976facbae63a0a149c7d_1_mwpm_03200403.jpeg\"]","news_url":"http://mini.eastday.com/mobile/170602113050410.html?qid=youmeng","type":"all","show_type":"3","publish_time":"06-02 11:30","source":"澎湃新闻","thumbnail_pic_s1":"http://08.imgmini.eastday.com/mobile/20170602/20170602113050_4a19f2bae05f976facbae63a0a149c7d_1_mwpm_03200403.jpeg"},{"id":"2483000","title":"波兰坠机身亡前总统棺材中被指存在其他人遗骸","author_name":"环球网","pics":"[\"http://07.imgmini.eastday.com/mobile/20170602/20170602113013_199f6eed29f1e0812cd1f37ed48dd38c_1_mwpm_03200403.jpeg\"]","news_url":"http://mini.eastday.com/mobile/170602113013962.html?qid=youmeng","type":"all","show_type":"3","publish_time":"06-02 11:30","source":"环球网","thumbnail_pic_s1":"http://07.imgmini.eastday.com/mobile/20170602/20170602113013_199f6eed29f1e0812cd1f37ed48dd38c_1_mwpm_03200403.jpeg"},{"id":"2482635","title":"专访转基因专项技术总师万建民：转基因安全性可控有科学结论","author_name":"科技日报","pics":"[\"http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_1_mwpm_03200403.jpeg\",\"http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_4_mwpm_03200403.jpeg\",\"http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_3_mwpm_03200403.jpeg\"]","news_url":"http://mini.eastday.com/mobile/170602112757008.html?qid=youmeng","type":"all","show_type":"4","publish_time":"06-02 11:27","source":"科技日报","thumbnail_pic_s1":"http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_1_mwpm_03200403.jpeg","thumbnail_pic_s2":"http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_4_mwpm_03200403.jpeg","thumbnail_pic_s3":"http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_3_mwpm_03200403.jpeg"},{"id":"2483001","title":"43人毕业班15对成婚系\u201c造假\u201d  新娘：我是去配合演戏的","author_name":"潘多拉小魔王","pics":"[\"http://09.imgmini.eastday.com/mobile/20170602/20170602_cff22874b3b2bb1d7c4d473dfc883d0f_mwpm_03200403.jpg\",\"http://09.imgmini.eastday.com/mobile/20170602/20170602_b15242672d88e1cf2b1560fa6114d186_mwpm_03200403.jpg\",\"http://09.imgmini.eastday.com/mobile/20170602/20170602_81effcc6a36162307cc42bdccb058384_mwpm_03200403.jpg\"]","news_url":"http://mini.eastday.com/mobile/170602112532837.html?qid=youmeng","type":"all","show_type":"4","publish_time":"06-02 11:25","source":"潘多拉小魔王","thumbnail_pic_s1":"http://09.imgmini.eastday.com/mobile/20170602/20170602_cff22874b3b2bb1d7c4d473dfc883d0f_mwpm_03200403.jpg","thumbnail_pic_s2":"http://09.imgmini.eastday.com/mobile/20170602/20170602_b15242672d88e1cf2b1560fa6114d186_mwpm_03200403.jpg","thumbnail_pic_s3":"http://09.imgmini.eastday.com/mobile/20170602/20170602_81effcc6a36162307cc42bdccb058384_mwpm_03200403.jpg"},{"id":"2483002","title":"韩民调：除朝鲜外中国是地区最大威胁，六成国民对中国持负面看法","author_name":"采蘑菇的大队长","pics":"[\"http://04.imgmini.eastday.com/mobile/20170602/20170602_5869da3cbf4968bffe572d11fdd0d3f4_mwpm_03200403.jpg\",\"http://04.imgmini.eastday.com/mobile/20170602/20170602_e40425b5b3e9dc4b721a57adf786f34a_mwpm_03200403.jpg\",\"http://04.imgmini.eastday.com/mobile/20170602/20170602_e91b57480930eaa766fac15c4ef61e9b_mwpm_03200403.jpg\"]","news_url":"http://mini.eastday.com/mobile/170602112417322.html?qid=youmeng","type":"all","show_type":"4","publish_time":"06-02 11:24","source":"采蘑菇的大队长","thumbnail_pic_s1":"http://04.imgmini.eastday.com/mobile/20170602/20170602_5869da3cbf4968bffe572d11fdd0d3f4_mwpm_03200403.jpg","thumbnail_pic_s2":"http://04.imgmini.eastday.com/mobile/20170602/20170602_e40425b5b3e9dc4b721a57adf786f34a_mwpm_03200403.jpg","thumbnail_pic_s3":"http://04.imgmini.eastday.com/mobile/20170602/20170602_e91b57480930eaa766fac15c4ef61e9b_mwpm_03200403.jpg"},{"id":"2483003","title":"美拦截洲际导弹试验成功 到底哪个国家该紧张？","author_name":"军事在前沿","pics":"[\"http://07.imgmini.eastday.com/mobile/20170602/20170602_e151ea484a16427c90a6f827fd4ddd5f_cover_mwpm_03200403.jpeg\",\"http://07.imgmini.eastday.com/mobile/20170602/20170602_5a6262647ea543f9edf88bc64c835ceb_cover_mwpm_03200403.jpeg\",\"http://07.imgmini.eastday.com/mobile/20170602/20170602_1de4d0a7545181f6b01c5b7210aa0af3_cover_mwpm_03200403.jpeg\"]","news_url":"http://mini.eastday.com/mobile/170602112031617.html?qid=youmeng","type":"all","show_type":"4","publish_time":"06-02 11:20","source":"军事在前沿","thumbnail_pic_s1":"http://07.imgmini.eastday.com/mobile/20170602/20170602_e151ea484a16427c90a6f827fd4ddd5f_cover_mwpm_03200403.jpeg","thumbnail_pic_s2":"http://07.imgmini.eastday.com/mobile/20170602/20170602_5a6262647ea543f9edf88bc64c835ceb_cover_mwpm_03200403.jpeg","thumbnail_pic_s3":"http://07.imgmini.eastday.com/mobile/20170602/20170602_1de4d0a7545181f6b01c5b7210aa0af3_cover_mwpm_03200403.jpeg"},{"id":"2483004","title":"巨型眼镜王蛇藏身小区车库 长3米重4.6公斤","author_name":"云南网","pics":"[\"http://06.imgmini.eastday.com/mobile/20170602/20170602111733_7ee9753894018d0ee0c97b01f4db0cc9_1_mwpm_03200403.jpeg\"]","news_url":"http://mini.eastday.com/mobile/170602111733003.html?qid=youmeng","type":"all","show_type":"3","publish_time":"06-02 11:17","source":"云南网","thumbnail_pic_s1":"http://06.imgmini.eastday.com/mobile/20170602/20170602111733_7ee9753894018d0ee0c97b01f4db0cc9_1_mwpm_03200403.jpeg"}]
     * page : 257
     * count : 1794
     */

    private int code;
    private String         msg;
    private int            page;
    private int         count;
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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 2482999
         * title : 男子因感情纠纷持刀劫持前妻 民警赴现场解救人质
         * author_name : 澎湃新闻
         * pics : ["http://08.imgmini.eastday.com/mobile/20170602/20170602113050_4a19f2bae05f976facbae63a0a149c7d_1_mwpm_03200403.jpeg"]
         * news_url : http://mini.eastday.com/mobile/170602113050410.html?qid=youmeng
         * type : all
         * show_type : 3
         * publish_time : 06-02 11:30
         * source : 澎湃新闻
         * thumbnail_pic_s1 : http://08.imgmini.eastday.com/mobile/20170602/20170602113050_4a19f2bae05f976facbae63a0a149c7d_1_mwpm_03200403.jpeg
         * thumbnail_pic_s2 : http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_4_mwpm_03200403.jpeg
         * thumbnail_pic_s3 : http://08.imgmini.eastday.com/mobile/20170602/20170602112757_5bdac814fefb71d97da49ea0dc5d1ab5_3_mwpm_03200403.jpeg
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
