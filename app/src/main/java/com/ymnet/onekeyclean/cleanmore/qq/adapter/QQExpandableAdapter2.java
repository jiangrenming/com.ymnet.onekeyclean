package com.ymnet.onekeyclean.cleanmore.qq.adapter;


import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.QQConstants;
import com.ymnet.onekeyclean.cleanmore.qq.QQDetailActivity;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQReceiveMode;
import com.ymnet.onekeyclean.cleanmore.qq.presenter.QQDetailPresImpl;
import com.ymnet.onekeyclean.cleanmore.qq.presenter.QQDetailPresenter;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;

import java.util.List;


/**
 * Created by MajinBuu on 2017/5/4 0004.
 *
 * @overView QQ接收的文件adapter
 */

public class QQExpandableAdapter2 extends BaseExpandableListAdapter implements View.OnClickListener {

    private static final String TAG = "QQExpandableAdapter2";
    private LayoutInflater     inflater;
    private Resources          res;
    private QQDetailPresenter  presenter;
    private boolean            showVoice;
    private Activity           mActivty;
    private QQReceiveMode      mType;
    private List<ListDataMode> modes;

    public QQExpandableAdapter2(QQDetailActivity activity, QQDetailPresImpl presenter, QQReceiveMode type) {
        this.mActivty = activity;
        inflater = LayoutInflater.from(C.get());
        res = C.get().getResources();
        this.mType = type;
        this.presenter = presenter;
        if (type != null) {
            showVoice = type.getType() == QQConstants.QQ_TYPE_VOICE;
            modes = type.getReceiveFile();
        }
        inflater = LayoutInflater.from(C.get());
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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
        Log.d(TAG, "getChildView: 打印咯:"+childPosition+"/"+groupPosition);
        ListDataMode group = getGroup(groupPosition);

        WareFileInfo[] itemDatas = new WareFileInfo[1];
        List<WareFileInfo> content = group.getContent();
        itemDatas[0] = content.get(childPosition);


        ChildHolder ch;
        if (convertView == null) {
            ch = new ChildHolder();
            convertView = inflater.inflate(R.layout.receive_file_child_view, parent, false);
            ch.fileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            ch.fileSize = (TextView) convertView.findViewById(R.id.tv_file_size);
            ch.fileIcon = (ImageView) convertView.findViewById(R.id.iv_file_icon);
            ch.state = convertView.findViewById(R.id.iv_file_state);
            convertView.setTag(ch);
        } else {
            ch = (ChildHolder) convertView.getTag();
        }
        ch.fileName.setText(group.getContent().get(childPosition).fileName);
        ch.fileSize.setText(String.valueOf(FormatUtils.formatFileSize(group.getContent().get(childPosition).size)));

        switch (group.getContent().get(childPosition).getFileType()) {

            case EXE:
                ch.fileIcon.setImageResource(R.drawable.exe);
                break;
            case GIF:
                ch.fileIcon.setImageResource(R.drawable.gif);
                break;
            case RARs:
                ch.fileIcon.setImageResource(R.drawable.rar);
                break;
            case PICTURE:
                ch.fileIcon.setImageResource(R.drawable.picture);
                break;
            case DOC:
                ch.fileIcon.setImageResource(R.drawable.doc);
                break;
            case EXCEL:
                ch.fileIcon.setImageResource(R.drawable.excel);
                break;
            case PDF:
                ch.fileIcon.setImageResource(R.drawable.pdf);
                break;
            case PPT:
                ch.fileIcon.setImageResource(R.drawable.ppt);
                break;
            case TXT:
                ch.fileIcon.setImageResource(R.drawable.txt);
                break;
            case RADIO:
                ch.fileIcon.setImageResource(R.drawable.radio);
                break;
            case VIDEO:
                ch.fileIcon.setImageResource(R.drawable.video);
                break;
            default:
                ch.fileIcon.setImageResource(R.drawable.unknown);
                break;
        }
        ch.state.setTag(R.id.item_index, itemDatas[0]);
        ch.state.setOnClickListener(this);
        if (itemDatas[0].status) {
            ch.state.setSelected(true);
        } else {
            ch.state.setSelected(false);
        }
        /*if (itemDatas[0] != null) {
            setController(ch.sdv2, UriUtil.parseUriOrNull("file://" + itemDatas[2].path));
        }*/
        return convertView;
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

    public enum FileIconType {
        UNKNOWN, GIF, RARs, PICTURE, DOC, EXCEL, PDF, PPT, TXT, RADIO, VIDEO,EXE
    }

    static class ChildHolder {
        private TextView  fileName;
        private TextView  fileSize;
        private ImageView fileIcon;
        private View      state;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return modes == null ? 0 : modes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        try {
            List<WareFileInfo> list = modes.get(groupPosition).getContent();
            return list == null ? 0 : list.size();
        } catch (Exception e) {
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
        return group == null ? null : group.getContent().get(childPosition);
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

    static class GroupHolder {
        public TextView tv_time, tv_size;
        public ImageView cb_status;
    }
}
