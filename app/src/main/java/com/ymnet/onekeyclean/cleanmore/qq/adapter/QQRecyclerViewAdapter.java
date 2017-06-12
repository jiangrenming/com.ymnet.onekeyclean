package com.ymnet.onekeyclean.cleanmore.qq.adapter;

import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.QQConstants;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQContent;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileDefault;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQPicMode;
import com.ymnet.onekeyclean.cleanmore.qq.presenter.QQPresenter;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.QQUtil;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangduheng26 on 4/1/16.
 * override  QQ清理界面recyclerview
 */

public class QQRecyclerViewAdapter extends RecyclerViewPlus.HeaderFooterItemAdapter {
    private static final String TAG = "QQRecyclerViewAdapter";
    private boolean mIsRemove;
    private QQContent                 content;
    private RecyclerViewClickListener mRecyclerClickListener;
    private Resources                 resources;
    private QQPresenter               presenter;
    private boolean flag = true;

    public QQRecyclerViewAdapter(QQPresenter presenter, QQContent content, boolean isRemove) {
        this.presenter = presenter;
        this.content = content;
        this.mIsRemove = isRemove;
        resources = C.get().getResources();
    }

    public void setRecyclerListListener(RecyclerViewClickListener mRecyclerClickListener) {
        this.mRecyclerClickListener = mRecyclerClickListener;
    }

    @Override
    public ContentViewHolder onCreateContentView(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateContentView: 进来了:" + viewType);
        // TODO: 2017/6/9 0009 更改为统一的条目布局
       /* if (QQConstants.QQ_TYPE_DEFALUT == viewType || QQConstants.QQ_TYPE_RECEIVE == viewType) {
            View view = LayoutInflater.from(C.get()).inflate(R.layout.cardview_default, parent, false);
            return new InnerViewHolderDefalut(view, mRecyclerClickListener);
        } else if (QQConstants.QQ_TYPE_VOICE == viewType) {
            View view = LayoutInflater.from(C.get()).inflate(R.layout.cardview_voice, parent, false);
            return new InnerViewHolderVoice(view, mRecyclerClickListener);
        } else if (QQConstants.QQ_TYPE_PIC == viewType) {
            View view = LayoutInflater.from(C.get()).inflate(R.layout.cardview_pic, parent, false);
            return new InnerViewHolderPic(view, mRecyclerClickListener);
        }*/
        if (QQConstants.QQ_TYPE_DEFALUT == viewType) {
            View view = LayoutInflater.from(C.get()).inflate(R.layout.clean_over_item_defalut, parent, false);
            return new InnerViewHolderStandard(view, mRecyclerClickListener);
        } else {
            View view = LayoutInflater.from(C.get()).inflate(R.layout.clean_over_item, parent, false);
            return new InnerViewHolderOther(view, mRecyclerClickListener);
        }
    }

    @Override
    public int getContentItemCount() {
        Log.d(TAG, "getContentItemCount: " + content.length());
        return content.length();
    }

    @Override
    public int getContentItemViewType(int position) {
        Log.i(TAG, "item类型:" + content.getType(position) + "");
        return content.getType(position);
    }

    @Override
    public void onBindContentViewHolder(ContentViewHolder h, int position) {
        if (h != null && h instanceof InnerViewHolder) {
            InnerViewHolder holder = (InnerViewHolder) h;

            QQFileType chatFile = content.get(position);
            Log.d(TAG, "onBindContentViewHolder: " + chatFile);
            if (chatFile == null)
                return;
            holder.position = position;
            if (presenter.isEnd()) {
                //扫描完成
                holder.ll_scanning.setVisibility(View.GONE);
                holder.ll_main_content.setVisibility(View.VISIBLE);
                holder.tv_trust_info.setText(chatFile.getFileInfo());
                holder.tv_trust_name.setText(chatFile.getFileName());
                holder.itemView.setEnabled(true);
                holder.iv_item_icon.setImageResource(chatFile.getIconId());

                //TODO 更换为silveractivity中holder类型
                /*//QQ文件类型
                if (holder instanceof InnerViewHolderDefalut) {
                    configViewHolderDefault((InnerViewHolderDefalut) holder, chatFile);
                    //QQ音频类型
                } else if (holder instanceof InnerViewHolderVoice) {
                    configViewHolderVoice((InnerViewHolderVoice) holder, chatFile);
                    //QQ图片类型
                } else if (holder instanceof InnerViewHolderPic) {
                    configViewHolderPic((InnerViewHolderPic) holder, chatFile);
                }*/
                if (holder instanceof InnerViewHolderStandard) {
                    configViewHolderStandard((InnerViewHolderStandard) holder, chatFile);
                } else if (holder instanceof InnerViewHolderOther) {
                    configViewHolderOther((InnerViewHolderOther) holder, chatFile);
                }
            } else {
                //扫描中
                holder.ll_scanning.setVisibility(View.VISIBLE);
                holder.ll_main_content.setVisibility(View.GONE);
                holder.itemView.setEnabled(false);
                holder.tv_scanning_name.setText(chatFile.getFileName());
                holder.iv_scanning_icon.setImageResource(chatFile.getIconId());
                if (chatFile.isInEndAnim()) {
                    holder.junk_sort_item_apk_progress.setBackgroundDrawable(null);
                    holder.junk_sort_item_apk_progress.setImageResource(R.drawable.junk_scan_status_finish);
                } else {
                    holder.junk_sort_item_apk_progress.setImageDrawable(null);
                    holder.junk_sort_item_apk_progress.setBackgroundResource(R.drawable.progress_white_anim);
                }

            }

        }
    }

    private void configViewHolderOther(InnerViewHolderOther holder, QQFileType chatFile) {
        if (holder == null || chatFile == null)
            return;

        if (chatFile.getDeleteStatus() == QQFileType.DELETE_DEFAULT) {
            holder.tv_trust_size.setVisibility(View.VISIBLE);
            holder.tv_trust_size.setText(FormatUtils.formatFileSize(chatFile.getScanOldSize()));
        } else if (chatFile.getDeleteStatus() == QQFileType.DELETE_CLOSE) {
            //            holder.tv_trust_size.setText(FormatUtils.formatFileSize(chatFile.getCurrentSize()));
            holder.ll_main_content.setVisibility(View.GONE);

        }

    }

    private void configViewHolderStandard(InnerViewHolderStandard holder, QQFileType chatFile) {
        if (holder == null || chatFile == null)
            return;
        if (chatFile.getDeleteStatus() == QQFileType.DELETE_DEFAULT) {
            holder.tv_trust_size.setVisibility(View.VISIBLE);
            holder.tv_trust_size.setText(FormatUtils.formatFileSize(chatFile.getScanOldSize()));
        }
        Log.d(TAG, "mIsRemove:" + mIsRemove);
        /*if (mIsRemove) {
            holder.itemView.setVisibility(View.GONE);
            notifyDataSetChanged();
        }*/
    }

    private void configViewHolderPic(InnerViewHolderPic holder, QQFileType chatFile) {
        if (holder == null || chatFile == null)
            return;
        if (chatFile.getDeleteStatus() == QQFileType.DELETE_DEFAULT) {
            //            holder.btn_scan_status.setVisibility(View.VISIBLE);
            holder.ll_show_default.setVisibility(View.VISIBLE);
            holder.tv_trust_size.setVisibility(View.VISIBLE);
            holder.tv_trust_size.setText(FormatUtils.formatFileSize(chatFile.getScanOldSize()));
            holder.ll_result.setVisibility(View.GONE);
            /*if (chatFile.isInEndAnim()) {
                holder.btn_scan_status.setBackgroundResource(R.drawable.btn_clean_bg);
                holder.btn_scan_status.setTextColor(resources.getColor(R.color.white));
                holder.btn_scan_status.setText(R.string.qq_go_clean);
            } else {
                holder.btn_scan_status.setBackgroundResource(R.drawable.btn_scanning);
                holder.btn_scan_status.setTextColor(resources.getColor(R.color.radio_text_color));
                holder.btn_scan_status.setText(R.string.wechat_scanning);
            }*/
            List<WareFileInfo> paths = null;
            if (chatFile instanceof QQPicMode) {
                paths = getShowPaths((QQPicMode) chatFile);
            }
            if (paths != null) {
                /**
                 * 不用处理holder的状态 这里不可能复用
                 */
                if (paths.size() > 2) {
                    Glide.with(C.get())
                            .load("file://" + paths.get(0).path)
                            .placeholder(R.drawable.image_item_griw_default)
                            .error(R.drawable.image_item_griw_default)
                            .centerCrop()
                            .into(holder.sdv1);
                    checkSuffix(paths.get(0).path, holder.iv_video_play1);
                    Glide.with(C.get())
                            .load("file://" + paths.get(1).path)
                            .placeholder(R.drawable.image_item_griw_default)
                            .error(R.drawable.image_item_griw_default)
                            .centerCrop()
                            .into(holder.sdv2);
                    checkSuffix(paths.get(1).path, holder.iv_video_play2);
                    Glide.with(C.get())
                            .load("file://" + paths.get(2).path)
                            .placeholder(R.drawable.image_item_griw_default)
                            .error(R.drawable.image_item_griw_default)
                            .centerCrop()
                            .into(holder.sdv3);
                    checkSuffix(paths.get(2).path, holder.iv_video_play3);

                } else if (paths.size() > 1) {
                    Glide.with(C.get())
                            .load("file://" + paths.get(0).path)
                            .placeholder(R.drawable.image_item_griw_default)
                            .error(R.drawable.image_item_griw_default)
                            .centerCrop()
                            .into(holder.sdv1);
                    checkSuffix(paths.get(0).path, holder.iv_video_play1);
                    Glide.with(C.get())
                            .load("file://" + paths.get(1).path)
                            .placeholder(R.drawable.image_item_griw_default)
                            .error(R.drawable.image_item_griw_default)
                            .centerCrop()
                            .into(holder.sdv2);
                    checkSuffix(paths.get(1).path, holder.iv_video_play2);
                    holder.fl3.setVisibility(View.INVISIBLE);

                } else if (paths.size() > 0) {
                    Glide.with(C.get())
                            .load("file://" + paths.get(0).path)
                            .placeholder(R.drawable.image_item_griw_default)
                            .error(R.drawable.image_item_griw_default)
                            .centerCrop()
                            .into(holder.sdv1);
                    checkSuffix(paths.get(0).path, holder.iv_video_play1);
                    holder.fl2.setVisibility(View.INVISIBLE);
                    holder.fl3.setVisibility(View.INVISIBLE);
                }

            }
        } else if (chatFile.getDeleteStatus() == QQFileType.DELETE_CLOSE) {
            //            holder.btn_scan_status.setVisibility(View.GONE);
            holder.ll_show_default.setVisibility(View.GONE);
            holder.tv_trust_size.setVisibility(View.GONE);
            holder.ll_result.setVisibility(View.VISIBLE);
            holder.tv_clean_size.setText(Html.fromHtml(resources.getString(R.string.wechat_free_spcae, holder.tv_trust_size.getText())));
            holder.iv_icon.setImageResource(chatFile.getIconId());
        }
    }

    private void checkSuffix(String path, View iv_video_play1) {
        if (TextUtils.isEmpty(path) || iv_video_play1 == null) {
            return;
        }
        if (path.endsWith("mp4")) {
            iv_video_play1.setVisibility(View.VISIBLE);
        } else {
            iv_video_play1.setVisibility(View.GONE);

        }
    }

    private void configViewHolderVoice(InnerViewHolderVoice holder, QQFileType chatFile) {
        if (holder == null || chatFile == null)
            return;
        if (chatFile.getDeleteStatus() == QQFileType.DELETE_DEFAULT) {
            //            holder.btn_scan_status.setVisibility(View.VISIBLE);
            holder.ll_show_default.setVisibility(View.VISIBLE);
            holder.tv_trust_size.setVisibility(View.VISIBLE);
            holder.tv_trust_size.setText(FormatUtils.formatFileSize(chatFile.getScanOldSize()));
            holder.ll_result.setVisibility(View.GONE);
            if (chatFile.isInEndAnim()) {
                /*holder.btn_scan_status.setBackgroundResource(R.drawable.btn_clean_bg);
                holder.btn_scan_status.setTextColor(resources.getColor(R.color.white));
                holder.btn_scan_status.setText(R.string.qq_go_clean);*/
            } else {
               /* holder.btn_scan_status.setBackgroundResource(R.drawable.btn_scanning);
                holder.btn_scan_status.setTextColor(resources.getColor(R.color.radio_text_color));
                holder.btn_scan_status.setText("扫描中");*/
            }
        } else if (chatFile.getDeleteStatus() == QQFileType.DELETE_CLOSE) {
            //            holder.btn_scan_status.setVisibility(View.GONE);
            holder.ll_show_default.setVisibility(View.GONE);
            holder.tv_trust_size.setVisibility(View.GONE);
            holder.ll_result.setVisibility(View.VISIBLE);
            holder.tv_clean_size.setText(Html.fromHtml(resources.getString(R.string.wechat_free_spcae, holder.tv_trust_size.getText())));
            holder.iv_icon.setImageResource(chatFile.getIconId());
        }

    }

    private void configViewHolderDefault(InnerViewHolderDefalut holder, QQFileType chatFile) {
        if (holder == null || chatFile == null)
            return;
        holder.tv_trust_del_result.setText(chatFile.getFileDelEffect());
        if (chatFile.getIconId() != -1) {
            holder.iv_icon.setImageResource(chatFile.getIconId());
        }
        holder.ll_clean_result.setVisibility(View.GONE);
        holder.ll_defalut.setVisibility(View.GONE);
        holder.ll_show_cleaning.setVisibility(View.GONE);
        if (QQFileType.DELETE_CLOSE == chatFile.getDeleteStatus()) {
            //删除完成
            holder.tv_trust_size.setVisibility(View.GONE);
            //            holder.btn_scan_status.setVisibility(View.GONE);
            holder.ll_clean_result.setVisibility(View.VISIBLE);
            holder.tv_clean_size.setText(Html.fromHtml(resources.getString(R.string.wechat_free_spcae, holder.tv_trust_size.getText())));
            com.ymnet.onekeyclean.cleanmore.utils.ToastUtil.showToastForShort("删除成功");
        } else if (QQFileType.DELETE_ING == chatFile.getDeleteStatus()) {
            //正在删除
            holder.tv_trust_size.setVisibility(View.GONE);
            //            holder.btn_scan_status.setVisibility(View.GONE);
            holder.ll_show_cleaning.setVisibility(View.VISIBLE);
            if (chatFile instanceof QQFileDefault) {
                holder.pb_cleaning.setProgress(100 - QQUtil.percent(chatFile.getCurrentSize(), chatFile.getScanOldSize()));

            }
        } else {
            //普通状态
            holder.tv_trust_size.setVisibility(View.VISIBLE);
            holder.tv_trust_size.setText(FormatUtils.formatFileSize(chatFile.getScanOldSize()));
            holder.ll_defalut.setVisibility(View.VISIBLE);
            /*holder.btn_scan_status.setVisibility(View.VISIBLE);*/
            Log.i("Tagc", chatFile.isInEndAnim() + "");
            Log.d(TAG, "configViewHolderDefault: " + chatFile.getType());
            /*if (chatFile.getType() == QQConstants.QQ_TYPE_RECEIVE) {
                if (chatFile.isInEndAnim()) {
                    holder.btn_scan_status.setBackgroundResource(R.drawable.btn_clean_bg);
                    holder.btn_scan_status.setTextColor(resources.getColor(R.color.white));
                    holder.btn_scan_status.setText(R.string.qq_go_clean);
                } else {
                    holder.btn_scan_status.setBackgroundResource(R.drawable.btn_scanning);
                    holder.btn_scan_status.setTextColor(resources.getColor(R.color.radio_text_color));
                    holder.btn_scan_status.setText(R.string.wechat_scanning);
                }
            } else if (chatFile.getType() == QQConstants.QQ_TYPE_DEFALUT) {
                if (chatFile.isInEndAnim()) {
                    holder.btn_scan_status.setBackgroundResource(R.drawable.btn_clean_bg);
                    holder.btn_scan_status.setTextColor(resources.getColor(R.color.white));
                    holder.btn_scan_status.setText(R.string.wechat_go_clean);
                } else {
                    holder.btn_scan_status.setBackgroundResource(R.drawable.btn_scanning);
                    holder.btn_scan_status.setTextColor(resources.getColor(R.color.radio_text_color));
                    holder.btn_scan_status.setText(R.string.wechat_scanning);
                }
            }*/

        }
    }


    private List<WareFileInfo> getShowPaths(QQPicMode mode) {
        List<WareFileInfo> paths = new ArrayList<>();
        if (mode != null) {
            ListDataMode one_before = mode.get(QQConstants.QQ_TIME_STATUE_ONE_BEFORE);
            if (one_before != null)
                paths.addAll(one_before.getContent());
            if (paths.size() < 3) {
                ListDataMode one = mode.get(QQConstants.QQ_TIME_STATUE_ONE);
                if (one != null)
                    paths.addAll(one.getContent());
            }
            if (paths.size() < 3) {
                ListDataMode three = mode.get(QQConstants.QQ_TIME_STATUE_THREE);
                if (three != null)
                    paths.addAll(three.getContent());
            }
            if (paths.size() < 3) {
                ListDataMode six = mode.get(QQConstants.QQ_TIME_STATUE_SIX);
                if (six != null)
                    paths.addAll(six.getContent());
            }
        }
        return paths;

    }

    abstract class InnerViewHolder extends ContentViewHolder {
        public TextView tv_trust_name, tv_trust_size, tv_trust_info;
        //        public TextView btn_scan_status;

        public View ll_scanning, ll_main_content;
        public ImageView iv_scanning_icon, iv_item_icon;
        public TextView  tv_scanning_name;
        public ImageView junk_sort_item_apk_progress;
        public int position = -1;

        public InnerViewHolder(View itemView, final RecyclerViewClickListener onClickListener) {
            super(itemView);
            ll_scanning = itemView.findViewById(R.id.ll_scanning);
            ll_main_content = itemView.findViewById(R.id.ll_main_content);

            iv_scanning_icon = (ImageView) ll_scanning.findViewById(R.id.iv_scanning_icon);
            tv_scanning_name = (TextView) ll_scanning.findViewById(R.id.tv_scanning_name);
            junk_sort_item_apk_progress = (ImageView) itemView.findViewById(R.id.junk_sort_item_apk_progress);

            tv_trust_name = (TextView) itemView.findViewById(R.id.tv_trust_name);
            tv_trust_size = (TextView) itemView.findViewById(R.id.tv_trust_size);
            tv_trust_info = (TextView) itemView.findViewById(R.id.tv_trust_info);
            iv_item_icon = (ImageView) itemView.findViewById(R.id.iv_item_icon);
            //                        btn_scan_status = (TextView) itemView.findViewById(btn_scan_status);

        }
    }

    class InnerViewHolderDefalut extends InnerViewHolder {
        public ImageView iv_icon;
        public TextView  tv_trust_del_result;
        public View      ll_defalut, ll_show_cleaning, ll_clean_result;
        public ProgressBar pb_cleaning;
        public TextView    tv_cleaning, tv_clean_size;

        public InnerViewHolderDefalut(View itemView, final RecyclerViewClickListener onClickListener) {
            super(itemView, onClickListener);
            tv_trust_del_result = (TextView) itemView.findViewById(R.id.tv_trust_del_result);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            ll_defalut = itemView.findViewById(R.id.ll_defalut);
            ll_show_cleaning = itemView.findViewById(R.id.ll_show_cleaning);
            ll_clean_result = itemView.findViewById(R.id.ll_clean_result);
            pb_cleaning = (ProgressBar) itemView.findViewById(R.id.pb_cleaning);
            tv_cleaning = (TextView) itemView.findViewById(R.id.tv_cleaning);
            tv_clean_size = (TextView) itemView.findViewById(R.id.tv_clean_size);
            Log.i("Tagnn", "ww");
            if (onClickListener != null) {
               /* btn_scan_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != -1) {
                            onClickListener.onClick(v, position);
                        }
                    }
                });*/
            }

        }
    }

    class InnerViewHolderVoice extends InnerViewHolder {
        public LinearLayout ll_result, ll_show_default;
        public ImageView iv_icon;
        public TextView  tv_clean_size;

        public InnerViewHolderVoice(View itemView, final RecyclerViewClickListener onClickListener) {
            super(itemView, onClickListener);
            ll_show_default = (LinearLayout) itemView.findViewById(R.id.ll_show_default);
            ll_result = (LinearLayout) itemView.findViewById(R.id.ll_result);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_clean_size = (TextView) itemView.findViewById(R.id.tv_clean_size);
            if (onClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != -1) {
                            onClickListener.onClick(v, position);
                        }
                    }
                });
                /*btn_scan_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != -1) {
                            onClickListener.onClick(v, position);
                        }
                    }
                });*/
            }
        }
    }

    class InnerViewHolderPic extends InnerViewHolder {
        public View fl1, fl2, fl3, iv_video_play1, iv_video_play2, iv_video_play3;
        public View ll_result, ll_show_default;
        public ImageView iv_icon, sdv1, sdv2, sdv3;
        public TextView tv_clean_size;

        public InnerViewHolderPic(View itemView, final RecyclerViewClickListener onClickListener) {
            super(itemView, onClickListener);
            ll_show_default = itemView.findViewById(R.id.ll_show_default);
            ll_result = itemView.findViewById(R.id.ll_result);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_clean_size = (TextView) itemView.findViewById(R.id.tv_clean_size);
            if (onClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != -1) {
                            onClickListener.onClick(v, position);
                        }
                    }
                });
                /*btn_scan_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != -1) {
                            onClickListener.onClick(v, position);
                        }
                    }
                });*/
            }
            sdv1 = (ImageView) itemView.findViewById(R.id.sdv0);
            sdv2 = (ImageView) itemView.findViewById(R.id.sdv1);
            sdv3 = (ImageView) itemView.findViewById(R.id.sdv2);
            fl1 = itemView.findViewById(R.id.fl0);
            fl2 = itemView.findViewById(R.id.fl1);
            fl3 = itemView.findViewById(R.id.fl2);
            iv_video_play1 = itemView.findViewById(R.id.iv_video_play0);
            iv_video_play2 = itemView.findViewById(R.id.iv_video_play1);
            iv_video_play3 = itemView.findViewById(R.id.iv_video_play2);

        }
    }

    class InnerViewHolderStandard extends InnerViewHolder {

        public ImageView check;

        public InnerViewHolderStandard(View itemView, final RecyclerViewClickListener onClickListener) {
            super(itemView, onClickListener);
            final long size = content.getSize();

            check = (ImageView) itemView.findViewById(R.id.iv_item_check);
            check.setSelected(flag);
            /*if (onClickListener != null) {
                ll_main_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != -1) {
                            onClickListener.onClick(v, position);
                        }
                    }
                });
            }*/
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long selectSize;
                    check.setSelected(!flag);
                    if (flag) {
                        //                        content.setSize(size + content.get(0).getScanOldSize());
                        selectSize=  size - content.get(0).getScanOldSize();
                    } else {
                        //                        content.setSize(size);
                        selectSize = size;
                    }
                    notifyDataSetChanged();
                    onClickListener.selectState(selectSize, !flag);
                    flag = !flag;

                }
            });
        }
    }

    class InnerViewHolderOther extends InnerViewHolder {

        public InnerViewHolderOther(View itemView, final RecyclerViewClickListener onClickListener) {
            super(itemView, onClickListener);
            if (onClickListener != null) {
                ll_main_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != -1) {
                            onClickListener.onClick(v, position);
                        }
                    }
                });
            }
        }
    }
}
