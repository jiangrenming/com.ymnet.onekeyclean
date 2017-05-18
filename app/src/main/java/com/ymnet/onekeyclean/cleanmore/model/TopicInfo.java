package com.ymnet.onekeyclean.cleanmore.model;

import java.util.List;

public class TopicInfo {
	
	public static final String TOPIC_ID = "topicId";
	public static final String TOPIC_TEMPLATE = "topicTemplate";

	/** 专题类型 : 模板 */
	public static final String TOPIC_ACTION_TYPE_SPECIFIC = "tpl";
	
	/** 专题类型 : web专题 */
	public static final String TOPIC_ACTION_TYPE_WEB = "web";

	/** 专题类型 : detail专题 */
	public static final String TOPIC_ACTION_TYPE_DETAIL = "detail";

	/**
	 * 模块专题
	 */
	public static final String TOPIC_ACTION_MODULE = "model";

	/** 模板1 */
	public static final int TEMPLATE_1_GRID = 1;
	
	/** 模板2 */
	public static final int TEMPLATE_2_LIST = 2;
	
	public String id;
	
	public String title;
	
	public String type;
	
	public String img_url;
	
	public String third_url;
	
	public String template;

	public int softId;

	public int disable_link;

	public String banner_img_url;
	
	public List<TopicChapterInfo> chapter;
	
}
