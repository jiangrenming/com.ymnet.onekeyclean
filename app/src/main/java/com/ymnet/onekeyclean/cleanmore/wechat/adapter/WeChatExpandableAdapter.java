package com.ymnet.onekeyclean.cleanmore.wechat.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.WeChatConstants;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.DateUtils;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.detail.PicDetailActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.device.DeviceInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatPicMode;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.WeChatDetailPresenter;

import java.io.File;
import java.util.List;

/**
 * Created by wangduheng26 on 4/7/16.
 */
public class WeChatExpandableAdapter extends BaseExpandableListAdapter implements View.OnClickListener {

    private LayoutInflater        inflater;
    private Resources             res;
    private WeChatDetailPresenter presenter;
    private boolean               showVoice;
    private Activity              mActivity;
    private int height = 0;
    private List<ListDataMode> modes;

    public WeChatExpandableAdapter(Activity activity, WeChatDetailPresenter presenter, WeChatPicMode type) {
        mActivity = activity;
        inflater = LayoutInflater.from(C.get());
        res = C.get().getResources();
        this.presenter = presenter;
        if (type != null) {
            showVoice = type.getType() == WeChatConstants.WECHAT_TYPE_VOICE;
            modes = type.getPics();
        }
        computeSdvWidth(activity);
    }

    private void computeSdvWidth(Activity activity) {
        height = (DeviceInfo.getScreenWidth(activity) - DisplayUtil.dip2px(activity, 12)) / 3;
    }

    @Override
    public int getGroupCount() {
        return modes == null ? 0 : modes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            List<WareFileInfo> list = modes.get(groupPosition).getContent();
            return list == null ? 0 : (int) Math.ceil((list.size() / 3.0));
        } catch (Exception e) {
            MobclickAgent.reportError(C.get(),"com.ymnet.onekeyclean.cleanmore.wechat.adapter.WeChatExpandableAdapter:"+e.toString());
            return 0;
        }

    }

    @Override
    public ListDataMode getGroup(int groupPosition) {
        return modes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ListDataMode group = getGroup(groupPosition);
        return group == null ? null : group.getContent().get(childPosition * 3);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ListDataMode group = getGroup(groupPosition);
        GroupHolder gh;
        if (convertView == null) {
            gh = new GroupHolder();
            convertView = inflater.inflate(R.layout.wechat_group_view, parent, false);
            gh.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            gh.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            gh.cb_status = (ImageView) convertView.findViewById(R.id.cb_status);
            convertView.setTag(gh);
        } else {
            gh = (GroupHolder) convertView.getTag();
        }
        gh.tv_time.setText(group.getName());
        gh.tv_size.setText(FormatUtils.formatFileSize(group.getCurrentSize()));

        if (group.isExpand()) {
            gh.tv_time.setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.junk_group_arrow_down), null);
        } else {
            gh.tv_time.setCompoundDrawablesWithIntrinsicBounds(null, null, res.getDrawable(R.drawable.junk_group_arrow_up), null);
        }
        final boolean currentStatus = checkDataSelectStatue(group.getContent());
        if (currentStatus) {
            gh.cb_status.setSelected(true);
        } else {
            gh.cb_status.setSelected(false);

        }
        gh.cb_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changeList(group.getContent(), !currentStatus);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private boolean checkDataSelectStatue(List<WareFileInfo> group) {
        if (group == null || group.size() == 0) {
            return false;
        }
        for (WareFileInfo info : group) {
            if (info != null && !info.status) {
                return false;
            }
        }
        return true;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ListDataMode group = getGroup(groupPosition);
        WareFileInfo[] itemDatas = new WareFileInfo[3];
        childPosition = childPosition * 3;
        try {
            List<WareFileInfo> content = group.getContent();
            itemDatas[0] = content.get(childPosition);
            itemDatas[1] = content.get(childPosition + 1);
            itemDatas[2] = content.get(childPosition + 2);
        } catch (IndexOutOfBoundsException iobe) {
            MobclickAgent.reportError(C.get(),"com.ymnet.onekeyclean.cleanmore.wechat.adapter.WeChatExpandableAdapter:"+iobe.toString());
        }


        ChildHolder ch;
        if (convertView == null) {
            ch = new ChildHolder();
            convertView = inflater.inflate(R.layout.wechat_child_view, parent, false);

            ch.fl0 = convertView.findViewById(R.id.fl0);
            ch.sdv0 = (ImageView) convertView.findViewById(R.id.sdv0);
            ch.play0 = convertView.findViewById(R.id.iv_video_play0);
            ch.cb_status0 = (ImageView) convertView.findViewById(R.id.cb_status0);
            ch.tv_size0 = (TextView) convertView.findViewById(R.id.tv_size0);
            ch.tv_time0 = (TextView) convertView.findViewById(R.id.tv_time0);

            ch.fl1 = convertView.findViewById(R.id.fl1);
            ch.sdv1 = (ImageView) convertView.findViewById(R.id.sdv1);
            ch.play1 = convertView.findViewById(R.id.iv_video_play1);
            ch.cb_status1 = (ImageView) convertView.findViewById(R.id.cb_status1);
            ch.tv_size1 = (TextView) convertView.findViewById(R.id.tv_size1);
            ch.tv_time1 = (TextView) convertView.findViewById(R.id.tv_time1);

            ch.fl2 = convertView.findViewById(R.id.fl2);
            ch.sdv2 = (ImageView) convertView.findViewById(R.id.sdv2);
            ch.play2 = convertView.findViewById(R.id.iv_video_play2);
            ch.cb_status2 = (ImageView) convertView.findViewById(R.id.cb_status2);
            ch.tv_size2 = (TextView) convertView.findViewById(R.id.tv_size2);
            ch.tv_time2 = (TextView) convertView.findViewById(R.id.tv_time2);
            if (showVoice) {
                changeHierarchy(ch.sdv0);
                changeHierarchy(ch.sdv1);
                changeHierarchy(ch.sdv2);
            }

            convertView.setTag(ch);
        } else {
            ch = (ChildHolder) convertView.getTag();
        }
        if (itemDatas[0] != null) {
            ch.fl0.setVisibility(View.VISIBLE);
            ch.tv_size0.setText(FormatUtils.formatFileSize(itemDatas[0].size));
            ch.cb_status0.setTag(R.id.item_index, itemDatas[0]);
            ch.cb_status0.setOnClickListener(this);
            if (itemDatas[0].status) {
                ch.cb_status0.setSelected(true);
            } else {
                ch.cb_status0.setSelected(false);

            }
            if (showVoice) {
                ch.tv_time0.setVisibility(View.VISIBLE);
                ch.tv_time0.setText(DateUtils.long2DateSimple(itemDatas[0].time));
            } else {
                setExportAttributes(ch.tv_time0, itemDatas[0]);
                ch.fl0.setTag(R.id.item_index, itemDatas[0]);
                ch.fl0.setOnClickListener(getItemOnClickListener());
//                setController(ch.sdv0, UriUtil.parseUriOrNull("file://" + itemDatas[0].path));
                Glide.with(mActivity)
                        .load("file://" + itemDatas[0].path)
                        .placeholder(R.drawable.image_item_griw_default)
                        .error(R.drawable.image_item_griw_default)
                        .centerCrop()
                        .into(ch.sdv0);

            }
            if (itemDatas[0].path.endsWith("mp4")) {
                ch.play0.setVisibility(View.VISIBLE);
            } else {
                ch.play0.setVisibility(View.GONE);
            }


        } else {
            ch.fl0.setVisibility(View.INVISIBLE);
        }
        if (itemDatas[1] != null) {
            ch.fl1.setVisibility(View.VISIBLE);

            ch.tv_size1.setText(FormatUtils.formatFileSize(itemDatas[1].size));

            ch.cb_status1.setTag(R.id.item_index, itemDatas[1]);
            ch.cb_status1.setOnClickListener(this);
            if (itemDatas[1].status) {
                ch.cb_status1.setSelected(true);
            } else {
                ch.cb_status1.setSelected(false);

            }
            if (showVoice) {
                ch.tv_time1.setVisibility(View.VISIBLE);
                ch.tv_time1.setText(DateUtils.long2DateSimple(itemDatas[1].time));
            } else {
                setExportAttributes(ch.tv_time1, itemDatas[1]);

                ch.fl1.setTag(R.id.item_index, itemDatas[1]);
                ch.fl1.setOnClickListener(getItemOnClickListener());
//                setController(ch.sdv1, UriUtil.parseUriOrNull("file://" + itemDatas[1].path));
                Glide.with(mActivity)
                        .load("file://" + itemDatas[1].path)
                        .placeholder(R.drawable.image_item_griw_default)
                        .error(R.drawable.image_item_griw_default)
                        .centerCrop()
                        .into(ch.sdv1);
            }
            if (itemDatas[1].path.endsWith("mp4")) {
                ch.play1.setVisibility(View.VISIBLE);
            } else {
                ch.play1.setVisibility(View.GONE);
            }
        } else {
            ch.fl1.setVisibility(View.INVISIBLE);

        }
        if (itemDatas[2] != null) {
            ch.fl2.setVisibility(View.VISIBLE);
            ch.tv_size2.setText(FormatUtils.formatFileSize(itemDatas[2].size));

            ch.cb_status2.setTag(R.id.item_index, itemDatas[2]);
            ch.cb_status2.setOnClickListener(this);
            if (itemDatas[2].status) {
                ch.cb_status2.setSelected(true);
            } else {
                ch.cb_status2.setSelected(false);

            }
            if (showVoice) {
                ch.tv_time2.setVisibility(View.VISIBLE);
                ch.tv_time2.setText(DateUtils.long2DateSimple(itemDatas[2].time));
            } else {
                setExportAttributes(ch.tv_time2, itemDatas[2]);
                ch.fl2.setTag(R.id.item_index, itemDatas[2]);
                ch.fl2.setOnClickListener(getItemOnClickListener());
//                setController(ch.sdv2, UriUtil.parseUriOrNull("file://" + itemDatas[2].path));
                Glide.with(mActivity)
                        .load("file://" + itemDatas[2].path)
                        .placeholder(R.drawable.image_item_griw_default)
                        .error(R.drawable.image_item_griw_default)
                        .centerCrop()
                        .into(ch.sdv2);
            }
            if (itemDatas[2].path.endsWith("mp4")) {
                ch.play2.setVisibility(View.VISIBLE);
            } else {
                ch.play2.setVisibility(View.GONE);
            }
        } else {
            ch.fl2.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private void setExportAttributes(TextView tv_time0, WareFileInfo itemData) {
        if (tv_time0 == null || itemData == null) return;
        int status = itemData.getExportStatus();
        if (status == WareFileInfo.EXPORT_SUCCESS) {
            tv_time0.setVisibility(View.VISIBLE);
            tv_time0.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tv_time0.setTextColor(Color.WHITE);
            tv_time0.setText(R.string.has_export);
            tv_time0.setPadding(4, 2, 4, 2);
            tv_time0.setBackgroundColor(res.getColor(R.color.wechat_export_success_bg));
        } else if (status == WareFileInfo.EXPORT_FAILE) {
            tv_time0.setVisibility(View.VISIBLE);
            tv_time0.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tv_time0.setTextColor(Color.WHITE);
            tv_time0.setPadding(4, 2, 4, 2);
            tv_time0.setText(R.string.export_fail);
            tv_time0.setBackgroundColor(res.getColor(R.color.wechat_export_fail_bg));
        } else {
            tv_time0.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            Object tag = v.getTag(R.id.item_index);
            if (tag != null && tag instanceof WareFileInfo) {
                presenter.changeSingle((WareFileInfo) tag);
                notifyDataSetChanged();
            }
        }
    }


    private void changeHierarchy(ImageView dv) {
        if (dv == null) return;

        Glide.with(mActivity)
                .load(R.drawable.voice_default)
                .placeholder(R.drawable.voice_default)
                .error(R.drawable.image_item_griw_default)
                .centerCrop()
                .into(dv);

    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        ListDataMode group = getGroup(groupPosition);
        if (group != null) group.setExpand(true);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
        ListDataMode group = getGroup(groupPosition);
        if (group != null) group.setExpand(false);
    }

    private View.OnClickListener getItemOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    Object tag = v.getTag(R.id.item_index);
                    if (tag != null && tag instanceof WareFileInfo) {
                        String path = ((WareFileInfo) tag).path;
                        File file = new File(path);
                        if (!TextUtils.isEmpty(path) && file.exists()) {
                            if (path.endsWith("mp4")) {
                                Intent intent = new Intent();
                                String type = "video/*";
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file), type);
                                mActivity.startActivity(intent);
                            } else {
                                Intent i = new Intent(C.get(), PicDetailActivity.class);
                                i.putExtra(PicDetailActivity.EXTRA_PATH, path);
                                mActivity.startActivity(i);
                            }
                        }

                    }
                }
            }
        };
    }

    static class GroupHolder {
        public TextView tv_time, tv_size;
        public ImageView cb_status;
    }

    static class ChildHolder {
        public TextView tv_size0, tv_size1, tv_size2;
        public TextView tv_time0, tv_time1, tv_time2;

        public View fl0, fl1, fl2;
        public ImageView sdv0,sdv1,sdv2,cb_status0, cb_status1, cb_status2;
        public View play0, play1, play2;
    }
}
