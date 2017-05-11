package com.ymnet.onekeyclean.cleanmore.model;/*
package com.example.baidumapsevice.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.baidumapsevice.constants.TopicConstants;
import com.example.baidumapsevice.wechat.detail.DetailActivity;
import com.example.baidumapsevice.wechat.topic.TopicActivity;
import com.example.baidumapsevice.wechat.topic.WebViewTopicActivity;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.utils.TopicItem;

public class TopicUtils {
	private static final String TAG = TopicUtils.class.getSimpleName();

	public static void startTopicActivity(Context context, int topicId, int templateId) {
		Log.d(TAG, "startTopicActivity");
		Intent intent = new Intent(context, TopicActivity.class);
		intent.putExtra(TopicInfo.TOPIC_ID, topicId);
		intent.putExtra(TopicInfo.TOPIC_TEMPLATE, templateId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	*/
/*public static void startTopicActivity(Context context, int topicId, int templateId, int from) {
		Log.d(TAG, "startTopicActivity");
		Intent intent = new Intent(context, TopicActivity.class);
		intent.putExtra(TopicInfo.TOPIC_ID, topicId);
		intent.putExtra(TopicInfo.TOPIC_TEMPLATE, templateId);
		intent.putExtra("from",from);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}*//*


	public static void startTopicActivity(Context context,TopicItem item){
		Log.d(TAG, "startTopicActivity");
		Intent intent = new Intent(context, TopicActivity.class);
		intent.putExtra(TopicInfo.TOPIC_ID, item.topicId);
		intent.putExtra(TopicInfo.TOPIC_TEMPLATE, item.template);
		intent.putExtra(TopicConstants.FROM_KEY, TopicConstants.FROM_VALUE_SOFT_MID1);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	*/
/*public static void startTopicActivityForResult(Activity context, int topicId, int templateId) {
		Log.d(TAG, "startTopicActivity");
		Intent intent = new Intent(context, TopicActivity.class);
		intent.putExtra(TopicInfo.TOPIC_ID, topicId);
		intent.putExtra(TopicInfo.TOPIC_TEMPLATE, templateId);

		context.startActivityForResult(intent, NavigationActivity.STARTACTIVITYREQUEST);
	}*//*


	public static void startWebViewTopicActivity(Activity activity, int topicId) {
		Log.d(TAG, "startWebViewTopicActivity");
		Intent intent = new Intent(activity, WebViewTopicActivity.class);
		intent.putExtra(TopicInfo.TOPIC_ID, topicId);
		activity.startActivity(intent);
	}

	*/
/*public static void startWebViewTopicActivity(Activity activity, int topicId, int from) {
		Log.d(TAG, "startWebViewTopicActivity");
		Intent intent = new Intent(activity, WebViewTopicActivity.class);
		intent.putExtra(TopicInfo.TOPIC_ID, topicId);
		intent.putExtra("from",from);
		activity.startActivity(intent);
	}

	public static void startWebViewTopicActivityForResult(Activity activity, int topicId) {
		Log.d(TAG, "startWebViewTopicActivity");
		Intent intent = new Intent(activity, WebViewTopicActivity.class);
		intent.putExtra(TopicInfo.TOPIC_ID, topicId);
		activity.startActivityForResult(intent, NavigationActivity.STARTACTIVITYREQUEST);
	}*//*


	public static void startDetailTopicActivity(Context context, int softId) {
		Log.d(TAG, "startDetailTopicActivity");
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("sid", softId);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	*/
/*public static void startDetailTopicActivityForResult(Activity context, int softId) {
		Log.d(TAG, "startDetailTopicActivity");
		Intent intent = new Intent(context, DetailActivity.class);
		intent.putExtra("sid", softId);
		context.startActivityForResult(intent, NavigationActivity.STARTACTIVITYREQUEST);
	}
*//*

	public static void startTopic(Activity activity, TopicItem topicItem, String tag) {
		if (topicItem != null) {
			String type = topicItem.type;
			if (TopicInfo.TOPIC_ACTION_TYPE_WEB.equals(type)) {
				TopicUtils.startWebViewTopicActivity(activity, topicItem.topicId);
			} else if (TopicInfo.TOPIC_ACTION_TYPE_SPECIFIC.equals(type)) {

				if(topicItem.from == TopicConstants.FROM_VALUE_SOFT_MID1){
					TopicUtils.startTopicActivity(C.get(),topicItem);
				}else{
					TopicUtils.startTopicActivity(C.get(), topicItem.topicId, topicItem.template);
				}
			} else if (TopicInfo.TOPIC_ACTION_TYPE_DETAIL.equals(type)) {
				TopicUtils.startDetailTopicActivity(C.get(), topicItem.softId);
			}else if(TopicInfo.TOPIC_ACTION_MODULE.equals(type)){
				TopicUtils.startTopicActivity(C.get(), topicItem.topicId, topicItem.template);
			}

			*/
/*if (BaseFragment.TYPE_RECOMMEND.equals(tag)) {
				Statistics.onEvent(C.get(), StatisticEventContants.Banner_Main_id + topicItem.topicId);
			}else if (BaseFragment.TYPE_SOFT.equals(tag)) {
				Statistics.onEvent(C.get(), StatisticEventContants.Banner_Soft_id + topicItem.topicId);
			}else if (BaseFragment.TYPE_GAME.equals(tag)) {
				Statistics.onEvent(C.get(), StatisticEventContants.Banner_Game_id + topicItem.topicId);
			}else if (SpecialListFragment.FLOAT_ACT.equals(tag)){
				StatisticSpec.sendEvent(StatisticEventContants.main_list_float);
			}else if (BaseAppListFragmentSpecial.RANK_MORE.equals(tag)){
				Statistics.onEvent(C.get(), StatisticEventContants.Zhuanti_Applist_More+ topicItem.topicId);
			}
			StatisticSpec.sendEvent(StatisticEventContants.zhuanti_id + topicItem.topicId);*//*

		}

	}

	*/
/*public static void startLoginActivity(Context context) {
		Log.d(TAG, "startLoginActivity");
		Intent intent = new Intent(context, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void startBindPhoneActivity(Context context) {
		Log.d(TAG, "startBindPhoneActivity");
		Intent intent = new Intent(context, BindPhoneActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}*//*


}
*/
