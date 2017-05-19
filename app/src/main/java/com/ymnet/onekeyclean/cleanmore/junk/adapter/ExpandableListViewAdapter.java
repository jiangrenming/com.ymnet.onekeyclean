package com.ymnet.onekeyclean.cleanmore.junk.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.db.CleanTrustDBHelper;
import com.ymnet.onekeyclean.cleanmore.db.CleanTrustDBManager;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileDetailsActivity;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileDetailsActivityPaths;
import com.ymnet.onekeyclean.cleanmore.junk.ITEMTYPE;
import com.ymnet.onekeyclean.cleanmore.junk.ScanFinishFragment;
import com.ymnet.onekeyclean.cleanmore.junk.ScanHelp;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledAppAndRAM;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildApk;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCache;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCacheOfChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildResidual;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkGroup;
import com.ymnet.onekeyclean.cleanmore.shortcut.DeleteHelp;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.DateUtils;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;
import com.ymnet.onekeyclean.cleanmore.wechat.view.AddTrustDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private List<JunkGroup> dataList;

    private LayoutInflater inflater;

    private Activity activity;


    private Handler handler;

    private CleanTrustDBManager manager;

    //    private DisplayImageOptions options;

    private final int TYPE                = 2;
    private final int TYPE_CHILD          = 0;
    private final int TYPE_CHILD_OF_CHILD = 1;

    private static final String SCHEME = "package";

    public ExpandableListViewAdapter(Activity activity, List<JunkGroup> dataList,
                                     Handler handler) {
        this.activity = activity;
        this.dataList = dataList;
        inflater = LayoutInflater.from(activity.getApplicationContext());
        this.handler = handler;
        manager = new CleanTrustDBManager(activity.getApplicationContext());
        //        options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).showImageOnLoading(R.drawable.big_file_folder)
        //                .showImageOnFail(R.drawable.big_file_folder).build();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (dataList == null) {
            return null;
        }
        return dataList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        try {
            JunkGroup groupItem = dataList.get(groupPosition);
            GroupViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new GroupViewHolder();
                convertView = inflater.inflate(R.layout.junk_item_group, null);
                viewHolder.groupNameTV = (TextView) convertView.findViewById(R.id.header_name);
                viewHolder.groupSizeTV = (TextView) convertView.findViewById(R.id.header_count);
                viewHolder.groupImg = (ImageView) convertView.findViewById(R.id.header_icon);
                viewHolder.groupCb = (ImageView) convertView.findViewById(R.id.header_check);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GroupViewHolder) convertView.getTag();
            }
            viewHolder.groupCb.setOnClickListener(getGroupCheckBoxClickListener(groupItem));
            viewHolder.groupNameTV.setText(groupItem.getName());
            viewHolder.groupSizeTV.setText(FormatUtils.formatFileSize(groupItem.getSize()));
            if (isExpanded) {
                viewHolder.groupImg.setImageResource(R.drawable.junk_group_arrow_up);
            } else {
                viewHolder.groupImg.setImageResource(R.drawable.junk_group_arrow_down);
            }
            int select = checkSelect(groupItem);
            viewHolder.setSelect(select);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (convertView != null) {
            if (groupPosition == 0) {
                convertView.setPadding(0, 0, 0, 0);
            } else {
                convertView.setPadding(0, DisplayUtil.dip2px(C.get(), 5), 0, 0);
            }
        }
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        JunkGroup groupItem = dataList.get(groupPosition);
        if (groupItem == null || groupItem.getChildrenItems() == null
                || groupItem.getChildrenItems().isEmpty()) {
            return null;
        }
        return groupItem.getChildrenItems().get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == -1) {
            return 0;
        }
        try {
            if (dataList == null || dataList.size() == 0
                    || dataList.get(groupPosition).getChildrenItems() == null
                    || dataList.get(groupPosition).getChildrenItems().size() == 0) {
                return 0;
            }
        } catch (Exception e) {
            Log.i("wdh", e.toString());
            return 0;
        }

        return dataList.get(groupPosition).getChildrenItems().size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildTypeCount() {
        return TYPE;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        List<JunkChild> items = null;
        if (dataList != null && groupPosition < dataList.size() && groupPosition > -1) { // In case of IndexOutOfBoundsException
            items = dataList.get(groupPosition).getChildrenItems();
        }

        if (items != null && items.size() > 0 && items.get(childPosition) instanceof JunkChildCacheOfChild) {
            return TYPE_CHILD_OF_CHILD;
        } else {
            return TYPE_CHILD;
        }

    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final JunkChild childrenItem = (JunkChild) getChild(groupPosition, childPosition);
        if (childrenItem == null) {
            return null;
        }
        ViewHolder holder;
        int type = getChildType(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_CHILD:
                    convertView = inflater.inflate(R.layout.junk_item_child, null);
                    holder.tv_junk_name = (TextView) convertView.findViewById(R.id.junk_name);
                    holder.tv_junk_tip = (TextView) convertView.findViewById(R.id.junk_label);
                    holder.tv_junk_size = (TextView) convertView.findViewById(R.id.file_size);
                    holder.iv_junk_checkbox = (ImageView) convertView.findViewById(R.id.junk_child_check);
                    holder.iv_junk_icon = (ImageView) convertView.findViewById(R.id.junk_icon);
                    holder.iv_junk_icon_small = (ImageView) convertView.findViewById(R.id.junk_icon_small);
                    holder.fl_junk_subscript = (FrameLayout) convertView.findViewById(R.id.junk_child_subscript);
                    holder.ll_junk_layout = (LinearLayout) convertView.findViewById(R.id.junk_layout);
                    break;
                case TYPE_CHILD_OF_CHILD:
                    convertView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.junk_item_three_level, null);
                    holder.ll_child_three = (LinearLayout) convertView.findViewById(R.id.ll_three);
                    holder.tv_child_name = (TextView) convertView.findViewById(R.id.cache_type_name);
                    holder.tv_child_size = (TextView) convertView.findViewById(R.id.cache_type_size);
                    holder.iv_child_icon = (ImageView) convertView.findViewById(R.id.cache_type_icon);
                    holder.iv_child_checkBox = (ImageView) convertView.findViewById(R.id.cache_type_check);
                    break;
            }
            if (convertView != null)
                convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (childrenItem instanceof JunkChildCache
                || childrenItem instanceof JunkChildResidual
                || childrenItem instanceof JunkChildApk
                || childrenItem instanceof InstalledAppAndRAM) {
            holder.tv_junk_name.setText(childrenItem.name);
            holder.tv_junk_size.setText(FormatUtils.formatFileSize(childrenItem.size));
            holder.setSelect(childrenItem.getSelect());
            holder.iv_junk_checkbox.setOnClickListener(getCheckBoxOnClickListener(childrenItem, groupPosition));
            if (childrenItem instanceof JunkChildCache) {
                JunkChildCache cache = (JunkChildCache) childrenItem;
                holder.iv_junk_icon_small.setVisibility(View.VISIBLE);
                if (!cache.isExpanded) {
                    holder.fl_junk_subscript.setVisibility(View.VISIBLE);
                } else {
                    holder.fl_junk_subscript.setVisibility(View.GONE);
                }

                if (JunkChildCache.systemCachePackName.equals(cache.packageName)) {
                    holder.tv_junk_tip.setText(R.string.junk_suggest_title);
                    holder.ll_junk_layout.setOnLongClickListener(null);
                } else {
                    holder.tv_junk_size.setText(FormatUtils.formatFileSize(childrenItem.size));
                    holder.tv_junk_tip.setText(cache.tip);
                    holder.ll_junk_layout.setOnLongClickListener(getCacheOnLongClickListener(cache, groupPosition));
                }
                if (JunkChildCache.systemCachePackName.equals(cache.packageName)) {
                    holder.iv_junk_icon.setImageResource(R.drawable.system_cache_icon);
                } else if (JunkChildCache.adSdk.equals(cache.packageName)) {
                    holder.iv_junk_icon.setImageResource(R.drawable.big_file_folder);
                } else {

                    // TODO: 2017/4/26 0026 暂时注释掉:替换为小机器人
                    if (cache.icon == null) {
                        holder.iv_junk_icon.setImageResource(R.drawable.file_features_icon);
                    } else {
                        holder.iv_junk_icon.setImageDrawable(cache.icon);
                    }
                }
                holder.ll_junk_layout.setOnClickListener(getCacheOnClickListener(cache, groupPosition, childPosition));
            } else if (childrenItem instanceof JunkChildResidual) {
                JunkChildResidual residual = (JunkChildResidual) childrenItem;
                holder.iv_junk_icon.setImageResource(R.drawable.big_file_folder);
                holder.tv_junk_tip.setText(R.string.junk_suggest_title);
                holder.iv_junk_icon_small.setVisibility(View.GONE);
                holder.fl_junk_subscript.setVisibility(View.GONE);
                holder.ll_junk_layout.setOnClickListener(getResidualOnClickListener(residual, groupPosition));
                holder.ll_junk_layout.setOnLongClickListener(getResidualOnLongClickListener(residual, groupPosition));
            } else if (childrenItem instanceof JunkChildApk) {
                holder.iv_junk_icon_small.setVisibility(View.GONE);
                holder.fl_junk_subscript.setVisibility(View.GONE);
                JunkChildApk apk = (JunkChildApk) childrenItem;
                // TODO: 2017/4/26 0026 暂时注释掉: 替换为小机器人
                if (apk.icon == null) {
                    holder.iv_junk_icon.setImageResource(R.mipmap.robot);
                } else {
                    holder.iv_junk_icon.setImageDrawable(apk.icon);
                }
                String versionName = apk.versionName;
                int installType = apk.installedType;
                if (System.currentTimeMillis() - apk.fileTime < 24 * 60 * 60 * 1000) {
                    holder.tv_junk_tip.setText(Html.fromHtml(activity.getApplicationContext().getResources().getString(R.string.apk_new_download1, versionName)));
                } else {
                    switch (installType) {
                        case JunkChildApk.INSTALLED:
                            holder.tv_junk_tip.setText(activity.getResources().getString(R.string.apk_installed) + versionName);
                            break;
                        case JunkChildApk.INSTALLED_OLD:
                            holder.tv_junk_tip.setText(activity.getResources().getString(R.string.apk_installed) + versionName);
                            break;
                        case JunkChildApk.INSTALLED_UPDATE:
                            holder.tv_junk_tip.setText(activity.getResources().getString(R.string.apk_installed) + versionName);
                            break;
                        case JunkChildApk.UNINSTALLED:
                            holder.tv_junk_tip.setText(activity.getResources().getString(R.string.apk_not_installed) + versionName);
                            break;
                        case JunkChildApk.BREAK_APK:
                            holder.tv_junk_tip.setText(activity.getResources().getString(R.string.apk_break));
                            break;
                    }
                }

                holder.ll_junk_layout.setOnClickListener(getApkOnClickListener(apk, groupPosition));
                holder.ll_junk_layout.setOnLongClickListener(getApkOnLongClickListener(apk, groupPosition));
                //判断是否新下载
            } else if (childrenItem instanceof InstalledAppAndRAM) {
                InstalledAppAndRAM ram = (InstalledAppAndRAM) childrenItem;

                holder.fl_junk_subscript.setVisibility(View.GONE);
                if (ram.getApp().flag == 0) {//系统应用
                    holder.tv_junk_tip.setText(R.string.system_app_enable_clean);
                    holder.iv_junk_icon_small.setVisibility(View.GONE);
                    //                    holder.ll_junk_layout.setOnClickListener(null);
                } else if (ram.getApp().flag == 1) {//用户应用
                    holder.tv_junk_tip.setText(R.string.use_app_suggest_clean);
                    holder.iv_junk_icon_small.setVisibility(View.VISIBLE);
                }
                holder.ll_junk_layout.setOnClickListener(getRAMOnClickListener(ram, groupPosition));

                // TODO: 2017/4/26 0026 暂时注释掉: 替换为小机器人
                if (ram.icon == null) {
                    holder.iv_junk_icon.setImageResource(R.mipmap.robot);
                } else {
                    holder.iv_junk_icon.setImageDrawable(ram.icon);
                }
                holder.ll_junk_layout.setOnLongClickListener(getRAMOnLongClickListener(ram));
                //判断是否系统应用
            }

        } else if (childrenItem instanceof JunkChildCacheOfChild) {
            JunkChildCacheOfChild childOfChild = (JunkChildCacheOfChild) childrenItem;
            holder.tv_child_name.setText(childOfChild.name);
            holder.tv_child_size.setText(FormatUtils.formatFileSize(childOfChild.size));
            if (JunkChildCacheOfChild.SYSTEM_CACHE == childOfChild.type) {
                holder.iv_child_checkBox.setVisibility(View.INVISIBLE);
                holder.ll_child_three.setOnClickListener(null);
                holder.ll_child_three.setOnLongClickListener(null);
                // TODO: 2017/4/26 0026 暂时注释掉: 替换为小机器人
                if (childOfChild.icon == null) {
                    holder.iv_child_icon.setImageResource(R.mipmap.robot);
                } else {
                    holder.iv_child_icon.setImageDrawable(childOfChild.icon);
                }

            } else {
                holder.iv_child_checkBox.setVisibility(View.VISIBLE);
                holder.iv_child_icon.setImageResource(R.drawable.junk_app_logo_cache);
                holder.iv_child_checkBox.setSelected(childOfChild.getSelect() == 1);
                holder.ll_child_three.setOnClickListener(getChildOfChildOnClickListener(childOfChild, groupPosition));
                holder.ll_child_three.setOnLongClickListener(getChildOfChildOnLongClickListener(childOfChild, groupPosition));
                holder.iv_child_checkBox.setOnClickListener(getChildOfChildCheckBoxOnClickListener(childOfChild, groupPosition, childPosition));
            }
        }
        return convertView;
    }

    private OnClickListener getChildOfChildCheckBoxOnClickListener(final JunkChildCacheOfChild childOfChild, final int groupPosition, final int childPosition) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<JunkChild> childs = dataList.get(groupPosition).getChildrenItems();
                if (childOfChild.getSelect() == 1) {
                    //对select 取反
                    childOfChild.setSelect(0);
                    for (JunkChild child : childs) {
                        if (child instanceof JunkChildCache) {
                            JunkChildCache cache = (JunkChildCache) child;
                            if (cache.packageName.equals(childOfChild.packageName)) {
                                child.setSelect(checkSelect(cache));
                                break;
                            }
                        }
                    }
                } else {
                    //使选中
                    childOfChild.setSelect(1);
                    //检查二级项
                    JunkChildCache cache = getJunkChildCacheFromPackName(groupPosition, childOfChild.packageName);
                    //                    List<JunkChildCacheOfChild> list = cache.childCacheOfChild;
                    cache.setSelect(checkSelect(cache));
                    //                    if (checkSelectJunkChildCacheOfChild(list)) {
                    //                        cache.setSelect(1);
                    //                    }
                    //                    if (checkSelectGroup()) {
                    //                        dataList.get(groupPosition).setSelect(1);
                    //                    }
                }
                //检查组
                handleSendUpdateBtnNotifity();
                notifyDataSetChanged(false);
            }
        };
    }

    private OnClickListener getCheckBoxOnClickListener(final JunkChild childrenItem, final int groupPosition) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childrenItem.getSelect() == 1) {
                    //取消选中的时候连带父组一起取消选中
                    dataList.get(groupPosition).setSelect(0);
                    if (childrenItem instanceof JunkChildCache) {
                        childrenItem.setSelect(0);
                        //取消子项的全部选中
                        List<JunkChildCacheOfChild> childOfChilds = ((JunkChildCache) childrenItem).childCacheOfChild;
                        for (JunkChildCacheOfChild childOfChild : childOfChilds) {
                            childOfChild.setSelect(0);
                        }
                    } else {
                        //                        对select 取反
                        childrenItem.setSelect(0);
                    }
                } else {
                    //使选中
                    //                    checkGroup();检查组里面的子目录是否全部选中
                    if (childrenItem instanceof JunkChildCache) {
                        //子项的全部选中
                        childrenItem.setSelect(1);
                        List<JunkChildCacheOfChild> childOfChilds = ((JunkChildCache) childrenItem).childCacheOfChild;
                        for (JunkChildCacheOfChild childOfChild : childOfChilds) {
                            childOfChild.setSelect(1);
                        }
                    } else {
                        childrenItem.setSelect(1);
                    }
                }
                notifyDataSetChanged(false);
                handleSendUpdateBtnNotifity();
            }
        };
    }

    private View.OnLongClickListener getChildOfChildOnLongClickListener(final JunkChildCacheOfChild childOfChild, final int groupPosition) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AddTrustDialog trustDialog = DialogFactory.createTrustDialog(activity);
                trustDialog.show();
                // TODO: 2017/4/26 0026 暂时注释掉: 替换为小机器人
                trustDialog.getIcon().setImageResource(R.mipmap.robot);
                trustDialog.setName(childOfChild.name);
                trustDialog.setBtnOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trustDialog.dismiss();
                        ContentValues values = new ContentValues();
                        values.put(CleanTrustDBHelper.FILE_NAME,
                                childOfChild.path);
                        values.put(CleanTrustDBHelper.FILE_PATH,
                                childOfChild.path);
                        values.put(CleanTrustDBHelper.FILE_SIZE,
                                childOfChild.size);
                        values.put(CleanTrustDBHelper.FILE_TYPE,
                                ITEMTYPE.CACHE.ordinal());
                        if (manager.insert(values)) {
                            dataList.get(groupPosition).getChildrenItems().remove(childOfChild);//从界面上删除
                            JunkChildCache cache = getJunkChildCacheFromPackName(groupPosition, childOfChild.packageName);
                            cache.childCacheOfChild.remove(childOfChild);//从数据源中删除
                            cache.size -= childOfChild.size;
                            dataList.get(groupPosition).setSize(dataList.get(groupPosition).getSize() - childOfChild.size);
                            // 执行删除
                            notifyDataSetChanged(true);
                        }
                    }
                });
                trustDialog.setCanceledOnTouchOutside(true);
                return false;
            }
        };
    }

    private OnClickListener getChildOfChildOnClickListener(final JunkChildCacheOfChild childOfChild, final int groupPosition) {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = DialogFactory.createDialog(activity, R.layout.dialog_clean);
                //                View v = inflater.inflate(R.layout.dialog_clean, null);
                dialog.findViewById(R.id.rl).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.ll).setVisibility(View.GONE);

                TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
                TextView dialog_message = (TextView) dialog.findViewById(R.id.dialog_message);
                TextView dialog_size = (TextView) dialog.findViewById(R.id.dialog_size);
                TextView dialogCheckbox = (TextView) dialog.findViewById(R.id.dialogcheckbox);
                dialogCheckbox.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 进入文件详情页面
                        Intent i = new Intent(activity, FileDetailsActivity.class);
                        i.putExtra("title_name", childOfChild.name);
                        i.putExtra("dir", childOfChild.path);
                        activity.startActivity(i);
                    }
                });
                Button dialog_btn1 = (Button) dialog.findViewById(R.id.dialog_btn1);
                Button dialog_btn0 = (Button) dialog.findViewById(R.id.dialog_btn0);
                dialog_title.setText(childOfChild.name);
                String tip = childOfChild.fileTip;
                if (!TextUtils.isEmpty(tip)) {
                    dialog_message.setText(tip);
                } else {
                    dialog_message.setText(activity.getString(R.string.clean_suggest_default));
                }
                dialog_size.setText(activity.getString(R.string.size_format, FormatUtils.formatFileSize(childOfChild.size)));
                dialogCheckbox.setText(Html.fromHtml(activity.getString(
                        R.string.contain_number_file,
                        Util.getFileListCount(childOfChild.path) + "")));
                dialog_btn0.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        dataList.get(groupPosition).getChildrenItems().remove(childOfChild);//从界面上删除
                        JunkChildCache cache = getJunkChildCacheFromPackName(groupPosition, childOfChild.packageName);
                        cache.childCacheOfChild.remove(childOfChild);//从数据源中删除
                        cache.size -= childOfChild.size;
                        dataList.get(groupPosition).setSize(dataList.get(groupPosition).getSize() - childOfChild.size);
                        // 执行删除
                        notifyDataSetChanged(true);
                        //异步删除
                        Util.asynchronousDeleteFolderNoContainOuterFolder(new File(childOfChild.path));//删除文件
                    }
                });
                dialog_btn1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        };
    }

    /**
     * 内存垃圾的长按事件
     *
     * @param ram
     * @return
     */
    private View.OnLongClickListener getRAMOnLongClickListener(final InstalledAppAndRAM ram) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AddTrustDialog trustDialog = DialogFactory.createTrustDialog(activity);
                trustDialog.show();
                trustDialog.setBtnText(ram.getSelect() == 1 ? activity.getString(R.string.not_checked) : activity.getString(R.string.check));
                // TODO: 2017/4/26 0026 暂时注释掉: 替换为小机器人
                if (ram.icon == null) {
                    trustDialog.getIcon().setImageResource(R.mipmap.robot);
                } else {
                    trustDialog.getIcon().setImageDrawable(ram.icon);
                }
                trustDialog.setName(ram.name);
                trustDialog.setBtnOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trustDialog.dismiss();
                        ram.setSelect(1 ^ ram.getSelect());
                        notifyDataSetChanged(false);
                    }
                });
                trustDialog.setCanceledOnTouchOutside(true);
                return true;
            }
        };
    }

    /**
     * 内存垃圾的点击事件
     *
     * @param ram
     * @return
     */
    private OnClickListener getRAMOnClickListener(final InstalledAppAndRAM ram, final int groupPosition) {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = DialogFactory.createDialog(activity, R.layout.dialog_clean_ram);
                TextView dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
                TextView tv_ram_size = (TextView) dialog.findViewById(R.id.tv_ram_size);
                TextView tv_forced_stop = (TextView) dialog.findViewById(R.id.tv_forced_stop);

                ImageView iv_uninstall_app = (ImageView) dialog.findViewById(R.id.iv_uninstall_app);
                Button dialog_btn1 = (Button) dialog.findViewById(R.id.dialog_btn1);
                Button dialog_btn0 = (Button) dialog.findViewById(R.id.dialog_btn0);

                //                ImageLoader.getInstance().displayImage(MarketImageDownloader.INSTALLED_APP_SCHEME+ram.app.packageName,iv_icon);
                dialog_btn0.setText(R.string.clear);
                dialog_title.setText(ram.name);
                if (ram.getApp().flag == 0) {
                    tv_forced_stop.setVisibility(View.GONE);
                    iv_uninstall_app.setVisibility(View.GONE);
                    if (1 == ram.getSelect()) {
                        dialog_btn0.setText(R.string.not_checked);
                    } else {
                        dialog_btn0.setText(R.string.check);
                    }
                }
                tv_ram_size.setText(FormatUtils.formatFileSize(ram.size));
                tv_forced_stop.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        //进系统界面
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts(SCHEME, ram.app.packageName, null);
                            intent.setData(uri);
                            activity.startActivityForResult(intent, 0x11);
                        } catch (Exception e1) {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            try {
                                activity.startActivity(intent);
                            } catch (Exception e) {

                            }
                        }

                    }
                });

                iv_uninstall_app.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ram != null && ram.getApp() != null) {
                            //                            Uri packageURI = UriUtil.parseUriOrNull("package:" + ram.getApp().packageName);
                            Uri packageURI = Uri.parse("package:" + ram.getApp().packageName);
                            System.out.println("packageURI:" + packageURI);
                            Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
                            activity.startActivity(intent);
                            /*try {
                                URL url = new URL("package", null, ram.getApp().packageName);
                                Intent intent = new Intent(Intent.ACTION_DELETE, url);
                                activity.startActivity(intent);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }*/
                        }

                    }
                });
                dialog_btn0.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        // 执行删除
                        if (ram.getApp().flag == 0) {
                            ram.setSelect(1 ^ ram.getSelect());
                        } else {
                            dataList.get(groupPosition).getChildrenItems().remove(ram);
                            dataList.get(groupPosition).setSize(dataList.get(groupPosition).getSize() - ram.size);
                            DeleteHelp.killBackgroundProcess((activity.getApplicationContext()), ram);
                        }

                        notifyDataSetChanged(true);


                    }
                });
                dialog_btn1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        };
    }

    /**
     * apk目录的长按事件
     *
     * @param apk
     * @return
     */
    private View.OnLongClickListener getApkOnLongClickListener(final JunkChildApk apk, final int grouPosition) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AddTrustDialog trustDialog = DialogFactory.createTrustDialog(activity);
                trustDialog.show();
                // TODO: 2017/4/26 0026 暂时注释掉: 替换为小机器人
                if (apk.icon == null) {
                    trustDialog.getIcon().setImageResource(R.mipmap.robot);
                } else {
                    trustDialog.getIcon().setImageDrawable(apk.icon);
                }
                trustDialog.setName(apk.name);
                trustDialog.setBtnOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trustDialog.dismiss();
                        ContentValues values = new ContentValues();
                        values.put(CleanTrustDBHelper.FILE_NAME,
                                apk.path);
                        values.put(CleanTrustDBHelper.FILE_PATH,
                                apk.path);
                        values.put(CleanTrustDBHelper.FILE_SIZE,
                                apk.size);
                        values.put(CleanTrustDBHelper.FILE_TYPE,
                                ITEMTYPE.APKFILE.ordinal());
                        if (manager.insert(values)) {
                            dataList.get(grouPosition).getChildrenItems().remove(apk);
                            notifyDataSetChanged(true);
                        }
                    }
                });
                trustDialog.setCanceledOnTouchOutside(true);
                return true;
            }
        };
    }

    /**
     * apk目录的点击事件
     *
     * @param apk
     * @return
     */
    private OnClickListener getApkOnClickListener(final JunkChildApk apk, final int groupPosition) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                showApkFileDialog(apk, groupPosition);
            }
        };
    }

    /**
     * 残留目录的长按事件
     *
     * @param residual
     * @return
     */
    private View.OnLongClickListener getResidualOnLongClickListener(final JunkChildResidual residual, final int groupPosition) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AddTrustDialog trustDialog = DialogFactory.createTrustDialog(activity);
                trustDialog.show();
                trustDialog.setIcon(R.drawable.big_file_folder);
                trustDialog.setName(residual.name);
                trustDialog.setBtnOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trustDialog.dismiss();
                        ContentValues values = new ContentValues();
                        values.put(CleanTrustDBHelper.FILE_NAME,
                                residual.name);
                        values.put(CleanTrustDBHelper.FILE_PATH,
                                residual.packageName);
                        values.put(CleanTrustDBHelper.FILE_SIZE,
                                residual.size);
                        values.put(CleanTrustDBHelper.FILE_TYPE,
                                ITEMTYPE.REMAIN.ordinal());
                        if (manager.insert(values)) {
                            dataList.get(groupPosition).getChildrenItems().remove(residual);
                            notifyDataSetChanged(true);
                        }
                    }
                });
                trustDialog.setCanceledOnTouchOutside(true);
                return false;//显示添加白名单界面
            }
        };
    }

    /**
     * 残留目录的点击事件
     *
     * @param residual
     * @return
     */
    private OnClickListener getResidualOnClickListener(final JunkChildResidual residual, final int groupPosition) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                showResidualDialog(residual, groupPosition);//显示残留详情页面
            }
        };
    }

    /**
     * 二级缓存目录的长按事件
     *
     * @param cache
     * @param groupPosition
     * @return
     */
    private View.OnLongClickListener getCacheOnLongClickListener(final JunkChildCache cache, final int groupPosition) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AddTrustDialog trustDialog = DialogFactory.createTrustDialog(activity);
                trustDialog.show();
                // TODO: 2017/4/26 0026 暂时注释掉: 替换为小机器人
                if (cache.icon == null) {
                    trustDialog.getIcon().setImageResource(R.mipmap.robot);
                } else {
                    trustDialog.getIcon().setImageDrawable(cache.icon);
                }
                trustDialog.setName(cache.name);
                trustDialog.setBtnOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        trustDialog.dismiss();
                        ContentValues values = new ContentValues();
                        values.put(CleanTrustDBHelper.FILE_NAME,
                                cache.name);
                        values.put(CleanTrustDBHelper.FILE_PATH,
                                cache.packageName);//用包名来代替路径ss
                        values.put(CleanTrustDBHelper.FILE_SIZE,
                                cache.size);
                        values.put(CleanTrustDBHelper.FILE_TYPE,
                                ITEMTYPE.CACHE.ordinal());
                        if (manager.insert(values)) {
                            //                            Log.i("wdh", "白名单插入数据库成功");
                            if (cache.isExpanded) {// 如果二级目录是展开的 移除它的子项
                                dataList.get(groupPosition).getChildrenItems().removeAll(cache.childCacheOfChild);
                            }
                            dataList.get(groupPosition).getChildrenItems().remove(cache);
                            notifyDataSetChanged(true);
                        }
                    }
                });
                trustDialog.setCanceledOnTouchOutside(true);
                return true;
            }
        };
    }

    /**
     * 二级缓存的点击 展开收缩事件
     *
     * @param cache
     * @param groupPosttion
     * @param childPosition
     * @return
     */
    private OnClickListener getCacheOnClickListener(final JunkChildCache cache, final int groupPosttion, final int childPosition) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                List<JunkChild> childs = dataList.get(groupPosttion).getChildrenItems();
                List<JunkChildCacheOfChild> elementsToDel = ((JunkChildCache) childs.get(childPosition)).childCacheOfChild;
                if (cache.isExpanded) {
                    v.findViewById(R.id.junk_child_subscript).setVisibility(View.GONE);
                    cache.isExpanded = false;
                    //                    List<JunkChildCacheOfChild> elementsToDel = ((JunkChildCache) dataList.get(groupPosttion).getChildrenItems().get(childPosition)).childCacheOfChild;
                    childs.removeAll(elementsToDel);

                } else {
                    v.findViewById(R.id.junk_child_subscript).setVisibility(View.VISIBLE);
                    cache.isExpanded = true;
                    //                    List<JunkChildCacheOfChild> elementsToDel = ((JunkChildCache) dataList.get(groupPosttion).getChildrenItems().get(childPosition)).childCacheOfChild;
                    childs.addAll(childPosition + 1, elementsToDel);

                }
                notifyDataSetChanged(false);
            }
        };
    }


    public void notifyDataSetChanged(boolean isRefreshData) {
        if (isRefreshData) {
            for (Iterator<JunkGroup> iterator = dataList.iterator(); iterator.hasNext(); ) {
                JunkGroup group = iterator.next();
                if (group.getChildrenItems() == null || group.getChildrenItems().size() == 0) {
                    iterator.remove();
                } else {
                    List<JunkChild> childs = group.getChildrenItems();
                    for (JunkChild junkChild : childs) {
                        if (junkChild instanceof JunkChildCache && (((JunkChildCache) junkChild).childCacheOfChild == null || ((JunkChildCache) junkChild).childCacheOfChild.size() == 0)) {
                            childs.remove(junkChild);
                            break;

                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    private void showResidualDialog(final JunkChildResidual residual,
                                    final int groupPosition) {
        final Dialog dialog = DialogFactory.createDialog(activity, R.layout.dialog_clean);
        dialog.findViewById(R.id.rl).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.ll).setVisibility(View.GONE);
        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView dialog_message = (TextView) dialog
                .findViewById(R.id.dialog_message);
        TextView dialog_size = (TextView) dialog.findViewById(R.id.dialog_size);
        TextView dialogCheckbox = (TextView) dialog
                .findViewById(R.id.dialogcheckbox);
        dialogCheckbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 进入文件详情页面
                Intent i = new Intent(activity, FileDetailsActivityPaths.class);
                i.putExtra("title_name", residual.name);
                i.putStringArrayListExtra("dirs", (ArrayList<String>) residual.paths);
                activity.startActivity(i);
            }
        });
        Button dialog_btn1 = (Button) dialog.findViewById(R.id.dialog_btn1);
        Button dialog_btn0 = (Button) dialog.findViewById(R.id.dialog_btn0);
        dialog_title.setText(R.string.header_residual);
        String name = residual.name;
        if (!TextUtils.isEmpty(name)) {
            dialog_message.setText(name);
        } else {
            dialog_message.setText(activity.getString(R.string.clean_suggest_default));
        }
        dialog_size.setText(activity.getString(R.string.size_format, FormatUtils.formatFileSize(residual.size)));
        dialogCheckbox.setText(Html.fromHtml(activity.getString(
                R.string.contain_number_file,
                residual.paths.size() + "")));
        dialog_btn0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dataList.get(groupPosition).getChildrenItems().remove(residual);
                // 执行删除
                notifyDataSetChanged(true);
                Util.asynchronousDeleteFolders(residual.paths);//删除卸载残留
            }
        });
        dialog_btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showApkFileDialog(final JunkChildApk apk,
                                   final int groupPoistion) {
        final Dialog dialog = DialogFactory.createDialog(activity, R.layout.dialog_clean);
        dialog.findViewById(R.id.rl).setVisibility(View.GONE);
        dialog.findViewById(R.id.ll).setVisibility(View.VISIBLE);
        TextView dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView tv_file_size = (TextView) dialog.findViewById(R.id.tv_file_size);
        TextView tv_version = (TextView) dialog.findViewById(R.id.tv_version);
        TextView tv_date = (TextView) dialog.findViewById(R.id.tv_date);
        TextView tv_path = (TextView) dialog.findViewById(R.id.tv_path);
        TextView tv_go_details = (TextView) dialog.findViewById(R.id.tv_go_details);
        final String path = apk.path;
        tv_go_details.setOnClickListener(new OnClickListener() {
            //忽略此路径下的全部apk文件
            @Override
            public void onClick(View v) {
                dialog.cancel();
                File f = new File(path);
                if (f.exists()) {
                    String dir = f.getParent();
                    ContentValues values = new ContentValues();
                    values.put(CleanTrustDBHelper.FILE_NAME,
                            dir);
                    values.put(CleanTrustDBHelper.FILE_PATH,
                            dir);//用包名来代替路径ss
                    values.put(CleanTrustDBHelper.FILE_TYPE,
                            ITEMTYPE.APKFILE.ordinal());
                    manager.insert(values);//
                }
                removePathAllApk(path, groupPoistion);
                notifyDataSetChanged(true);
            }
        });
        Button dialog_btn1 = (Button) dialog.findViewById(R.id.dialog_btn1);
        Button dialog_btn0 = (Button) dialog.findViewById(R.id.dialog_btn0);
        dialog_btn0.setText(R.string.clear);
        dialog_title.setText(R.string.apk_file);
        tv_file_size.setText(FormatUtils.formatFileSize(apk.size));
        tv_version.setText(apk.versionName + "");

        tv_date.setText(DateUtils.long2DateSimple(apk.fileTime));
        tv_path.setText(path);
        dialog_btn0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                // 执行删除
                dataList.get(groupPoistion).getChildrenItems().remove(apk);
                dataList.get(groupPoistion).setSize(dataList.get(groupPoistion).getSize() - apk.size);
                notifyDataSetChanged(true);
                Util.asynchronousDeleteFolderNoContainOuterFolder(new File(path));


            }
        });
        dialog_btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        //        dialog.setContentView(v);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void removePathAllApk(String path, int groupPoistion) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File f = new File(path);
        if (!f.exists()) {
            return;
        }
        path = f.getParent();
        JunkGroup junkGroup = dataList.get(groupPoistion);
        List<JunkChild> childs = junkGroup.getChildrenItems();
        List<JunkChildApk> delApks = new ArrayList<JunkChildApk>();
        for (JunkChild child : childs) {
            if (child instanceof JunkChildApk) {
                if (((JunkChildApk) child).path.startsWith(path)) {
                    delApks.add((JunkChildApk) child);
                }
            }
        }
        childs.removeAll(delApks);
    }


    /**
     * 处理选中未选中后 button显示
     */
    private void handleSendUpdateBtnNotifity() {
        Message mes = Message.obtain();
        mes.what = ScanFinishFragment.ACTION_SELECTED_CHANGE;
        handler.sendMessage(mes);

    }


    /**
     * 一级目录checkbox的点击事件
     *
     * @param groupItem
     * @return
     */
    private OnClickListener getGroupCheckBoxClickListener(final JunkGroup groupItem) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                int select = groupItem.getSelect();
                groupItem.setSelect(1 ^ select);
                changChild(1 ^ select, groupItem);
                handleSendUpdateBtnNotifity();
                notifyDataSetChanged(false);
            }
        };
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @SuppressLint("NewApi")
    final static class GroupViewHolder {
        TextView  groupNameTV;
        TextView  groupSizeTV;
        ImageView groupImg;
        ImageView groupCb;

        public void setSelect(int state) {
            if (Build.VERSION.SDK_INT >= 11) {
                if (state == ScanHelp.STATE_ALL_SELECT) {
                    groupCb.setActivated(false);
                    groupCb.setSelected(true);

                } else if (state == ScanHelp.STATE_UNALL_SELECT) {
                    groupCb.setActivated(false);
                    groupCb.setSelected(false);
                } else if (state == ScanHelp.STATE_HALF_SELECT) {
                    groupCb.setActivated(true);
                }
            } else {
                if (state == ScanHelp.STATE_ALL_SELECT) {
                    groupCb.setImageResource(R.drawable.item_check);
                    //                    groupCb.setBackgroundResource(R.drawable.junk_checkbox_checked);
                } else if (state == ScanHelp.STATE_UNALL_SELECT) {
                    groupCb.setImageResource(R.drawable.item_notcheck);
                } else if (state == ScanHelp.STATE_HALF_SELECT) {
                    groupCb.setImageResource(R.drawable.junk_checkbox_halfselect);
                }
            }

        }
    }

    @SuppressLint("NewApi")
    final static class ViewHolder {
        TextView tv_junk_name, tv_junk_tip, tv_junk_size;
        ImageView iv_junk_checkbox, iv_junk_icon, iv_junk_icon_small;
        FrameLayout  fl_junk_subscript;
        LinearLayout ll_junk_layout;
        LinearLayout ll_child_three;
        TextView     tv_child_name, tv_child_size;
        ImageView iv_child_icon, iv_child_checkBox;

        public void setSelect(int state) {
            if (Build.VERSION.SDK_INT >= 11) {
                if (state == ScanHelp.STATE_ALL_SELECT) {
                    iv_junk_checkbox.setActivated(false);
                    iv_junk_checkbox.setSelected(true);

                } else if (state == ScanHelp.STATE_UNALL_SELECT) {
                    iv_junk_checkbox.setActivated(false);
                    iv_junk_checkbox.setSelected(false);
                } else if (state == ScanHelp.STATE_HALF_SELECT) {
                    iv_junk_checkbox.setActivated(true);
                }
            } else {
                if (state == ScanHelp.STATE_ALL_SELECT) {
                    iv_junk_checkbox.setImageResource(R.drawable.item_check);
                    //                    groupCb.setBackgroundResource(R.drawable.junk_checkbox_checked);
                } else if (state == ScanHelp.STATE_UNALL_SELECT) {
                    iv_junk_checkbox.setImageResource(R.drawable.item_notcheck);
                } else if (state == ScanHelp.STATE_HALF_SELECT) {
                    iv_junk_checkbox.setImageResource(R.drawable.junk_checkbox_halfselect);
                }
            }

        }
    }

    /**
     * @param select
     * @param groupItem
     */
    private void changChild(int select, JunkGroup groupItem) {
        List<JunkChild> childrenItems = groupItem.getChildrenItems();
        if (childrenItems != null && childrenItems.size() > 0) {
            for (JunkChild item : childrenItems) {
                if (item instanceof JunkChildCache) {
                    List<JunkChildCacheOfChild> childs = ((JunkChildCache) item).childCacheOfChild;
                    if (childs != null && childs.size() > 0) {
                        for (JunkChildCacheOfChild child : childs) {
                            child.setSelect(select);
                        }
                        item.setSelect(select);
                    }
                } else {
                    item.setSelect(select);

                }
            }
        }

    }

    /**
     * group select 状态检查
     *
     * @param group
     * @return
     */
    private int checkSelect(JunkGroup group) {
        List<JunkChild> list = group.getChildrenItems();
        if (list == null || list.size() == 0) {
            return ScanHelp.STATE_UNALL_SELECT;
        }
        JunkChild junkChild = list.get(0);
        if (ScanHelp.STATE_ALL_SELECT == junkChild.getSelect()) {
            //index 0 处于选中状态
            for (JunkChild child : list) {
                if (ScanHelp.STATE_ALL_SELECT != child.getSelect()) {
                    return ScanHelp.STATE_HALF_SELECT;
                }
            }
            return ScanHelp.STATE_ALL_SELECT;
        } else if (ScanHelp.STATE_UNALL_SELECT == junkChild.getSelect()) {
            //index 0 处于非选中状态
            for (JunkChild child : list) {
                if (ScanHelp.STATE_UNALL_SELECT != child.getSelect()) {
                    return ScanHelp.STATE_HALF_SELECT;
                }
            }
            return ScanHelp.STATE_UNALL_SELECT;
        } else if (ScanHelp.STATE_HALF_SELECT == junkChild.getSelect()) {
            return ScanHelp.STATE_HALF_SELECT;
        }
        return ScanHelp.STATE_UNALL_SELECT;
    }

    /**
     * 检查 cache select 状态
     *
     * @param cache
     * @return
     */
    private int checkSelect(JunkChildCache cache) {
        List<JunkChildCacheOfChild> list = cache.childCacheOfChild;
        if (list == null || list.size() == 0) {
            return ScanHelp.STATE_UNALL_SELECT;
        }
        JunkChildCacheOfChild cc = list.get(0);
        if (ScanHelp.STATE_ALL_SELECT == cc.getSelect()) {
            //index 0 处于选中状态
            for (JunkChild child : list) {
                if (ScanHelp.STATE_ALL_SELECT != child.getSelect()) {
                    return ScanHelp.STATE_HALF_SELECT;
                }
            }
            return ScanHelp.STATE_ALL_SELECT;
        } else if (ScanHelp.STATE_UNALL_SELECT == cc.getSelect()) {
            //index 0 处于非选中状态
            for (JunkChild child : list) {
                if (ScanHelp.STATE_UNALL_SELECT != child.getSelect()) {
                    return ScanHelp.STATE_HALF_SELECT;
                }
            }
            return ScanHelp.STATE_UNALL_SELECT;
        } else if (ScanHelp.STATE_HALF_SELECT == cc.getSelect()) {
            return ScanHelp.STATE_HALF_SELECT;
        }
        return ScanHelp.STATE_UNALL_SELECT;
    }


    /**
     * 从数据源中找出包名为name的JunkChildCache
     *
     * @param groupPosition
     * @param name
     * @return
     */
    private JunkChildCache getJunkChildCacheFromPackName(int groupPosition, String name) {
        List<JunkChild> childs = dataList.get(groupPosition).getChildrenItems();
        for (JunkChild child : childs) {
            if (child instanceof JunkChildCache) {
                if (((JunkChildCache) child).packageName.equals(name)) {
                    return (JunkChildCache) child;
                }
            }
        }
        return null;

    }
}
