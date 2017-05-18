package com.ymnet.onekeyclean.cleanmore.junk.adapter;/*
package com.example.baidumapsevice.junk.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.baidumapsevice.SessionManager;
import com.example.baidumapsevice.common.ApplicationUtils;
import com.example.baidumapsevice.customview.RecyclerViewPlus;
import com.example.baidumapsevice.datacenter.DataCenterObserver;
import com.example.baidumapsevice.datacenter.MarketObservable;
import com.example.baidumapsevice.datacenter.MarketObserver;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.utils.DisplayUtil;
import com.example.baidumapsevice.wechat.R;
import com.example.baidumapsevice.wechat.listener.RecyclerViewClickListener;
import com.facebook.common.util.UriUtil;

import java.util.List;

*/
/**
 * Created by wangdh on 5/26/16.
 * gmail:wangduheng26@gamil.com
 * 2345:wangdh@2345.com
 *//*

// TODO: 2017/4/27 0027 最后
public class RecommendAdapter extends RecyclerViewPlus.HeaderFooterItemAdapter implements MarketObserver {
    private RecyclerViewClickListener listener;
    private List<App>                 data;
    private Resources                 res;
    private Activity                  mActivity;
    private DataCenterObserver        session;
    private DownloadManager           mDownloadManager;
    public static final int TITLE = 0;
    public static final int CONTENT = 1;

    public RecommendAdapter(Activity activity, List<App> data) {
        this.mActivity = activity;
        this.data = data;
        res = C.get().getResources();
        session = DataCenterObserver.get(mActivity);
        session.addObserver(this);
        mDownloadManager = DownloadManager.getInstance(mActivity.getApplicationContext());
    }

    public void setRecyclerListListener(RecyclerViewClickListener mRecyclerClickListener) {
        this.listener = mRecyclerClickListener;
    }

    @Override
    protected int getContentItemViewType(int position) {
        if (position == 0) {
            return TITLE;
        } else {
            return CONTENT;
        }
    }

    @Override
    public int getContentItemCount() {
        return (data == null||data.isEmpty()) ? 0 : data.size() + 1;
    }

    @Override
    protected void onBindContentViewHolder(ContentViewHolder holder, int position) {
        if (holder instanceof TitleHolder) {
            if (((TitleHolder) holder).itemView instanceof TextView) {
                ((TextView) ((TitleHolder) holder).itemView).setText(R.string.single_game_recommend);
            }
        } else if (position > 0) {
            final int index = position - 1;
            AppItemViewHolder vh = (AppItemViewHolder) holder;
            App app = data.get(index);
            vh.iv_app_icon.setImageURI(UriUtil.parse(app.icon));
            AppListLableController.setTitleIcon(app.recomIco, vh.iv_recommend_icon);
            vh.tv_title.setText(app.title);
            vh.tv_title.requestLayout();

            if (!TextUtils.isEmpty(app.sLabel)) {
                vh.tv_label.setText(app.sLabel);
                vh.tv_label.setVisibility(View.VISIBLE);
            } else {
                vh.tv_label.setVisibility(View.GONE);
            }

            if (app.giftTotal > 0) {
                vh.tv_gift_label.setVisibility(View.VISIBLE);
            } else {
                vh.tv_gift_label.setVisibility(View.GONE);
            }
            vh.tv_size.setText(app.fileLength);
            vh.tv_download_count.setText(ApplicationUtils.getFormatDownloads(app.totalDowns));

            try {
                double m = Double.parseDouble(app.mark);
                if (m > 10) {
                    app.mark = "10.0";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(app.eventTitle)) {
                // show event
                vh.tv_introduce.setText(app.eventTitle);
                vh.tv_introduce.setTextColor(res.getColor(R.color.special_event));
                vh.tv_introduce.setCompoundDrawablesWithIntrinsicBounds(res.getDrawable(R.drawable.event_icon), null, null, null);
                vh.tv_introduce.setCompoundDrawablePadding(DisplayUtil.dpToPx(3, res));
            } else {
                // show oneword
                vh.tv_introduce.setText(app.oneword);
                if (0 == app.seoKeyColor) {
                    vh.tv_introduce.setTextColor(res.getColor(R.color.special_item_two));
                } else {
                    vh.tv_introduce.setTextColor(res.getColor(R.color.special_event));
                }
                vh.tv_introduce.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                vh.tv_introduce.setCompoundDrawablePadding(DisplayUtil.dpToPx(0, res));
            }

            // 列表中存在下载列表的任务，更新状态
            String packageName = app.packageName;

            vh.versioncode = app.versionCode;
            vh.packageName = packageName;
            vh.tv_download.setTag(R.id.download_item, app);
            vh.tv_download.setTag(R.id.download_url, app.url);
            vh.pb_progress.setTag(R.id.download_url, app.url);
            vh.tv_rate.setTag(R.id.download_url, app.url);
            vh.tv_introduce.setTag(R.id.download_url, app.url);
            vh.rl_size_download_count.setTag(R.id.download_url, app.url);
            vh.ll_download_size_speed.setTag(R.id.download_url, app.url);
            vh.tv_download_size.setTag(R.id.download_url, app.url);
            vh.tv_speed.setTag(R.id.download_url, app.url);
            ViewTagger.setTag(vh.tv_download, R.id.hold_activty, mActivity);
            mDownloadManager.setOnClickListener(vh.tv_download);
            AppsUtils.notifyDisplayEvent(packageName);

            DownloadInfo downloadInfo = mDownloadManager.getDownloadInfo(app.url);
            if (downloadInfo != null) {
                downloadInfo.addProgressViews(
                        vh.pb_progress,
                        vh.tv_download,
                        vh.tv_rate,
                        vh.tv_introduce
                        vh.rl_size_download_count,,
                        vh.ll_download_size_speed,
                        vh.tv_download_size,
                        vh.tv_speed);
                downloadInfo.notifyProgress(mActivity);
            } else {
                // 升级 or 打开 or 下载
                if (session.getAppsInfoHandler().checkInupdatelist(app.packageName)) {
                    // 升级
                    vh.tv_download.setText(R.string.update);
                    vh.tv_download.setTextColor(mActivity.getResources().getColor(R.color.item_update_color));
                    vh.tv_download.setBackgroundResource(R.drawable.install_bg);

                    //签名冲突
                    InstalledApp mInstalledApp = session.getInstalledApp(app.packageName); //In case of NullPointerException
                    if (!TextUtils.isEmpty(app.certMd5) && mInstalledApp != null && !mInstalledApp.signatures.contains(app.certMd5)) {
                        vh.tv_signature.setVisibility(View.VISIBLE);
                    } else {
                        vh.tv_signature.setVisibility(View.GONE);
                    }
                } else if (session.getAppsInfoHandler().checkIsHasInatall(app.packageName)) {
                    // 打开
                    vh.tv_download.setText(R.string.download_start);
                    vh.tv_download.setTextColor(mActivity.getResources().getColor(R.color.item_update_color));
                    vh.tv_download.setBackgroundResource(R.drawable.install_bg);
                    vh.tv_signature.setVisibility(View.GONE);
                } else {
                    vh.tv_download.setText(R.string.appstore_download);
                    vh.tv_download.setTextColor(mActivity.getResources().getColor(R.color.item_down_color));
                    vh.tv_download.setBackgroundResource(R.drawable.item_down);
                    vh.tv_signature.setVisibility(View.GONE);
                    vh.tv_download.setTag(R.id.download_result_click,new DetailActivity.DownloadClickCallBack(){
                        @Override
                        public void clickCallBack() {
                            StatisticSpec.sendEvent(StatisticEventContants.clean_finish_recommend_list_download);
                        }

                        @Override
                        public void clickCallBack(int position) {
                        }
                    });
                }

                vh.tv_download.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                vh.pb_progress.setVisibility(View.GONE);
                vh.tv_rate.setVisibility(View.INVISIBLE);
                vh.tv_introduce.setVisibility(View.VISIBLE);
                vh.rl_size_download_count.setVisibility(View.VISIBLE);
                vh.ll_download_size_speed.setVisibility(View.GONE);
            }
            if (listener != null) {
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v, index);
                    }
                });
            }

            vh.itemView.setTag(vh);
        }
    }

    @Override
    public ContentViewHolder onCreateContentView(ViewGroup parent, int viewType) {
        if (viewType == TITLE) {
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView title = new TextView(mActivity);
            title.setLayoutParams(lp);
            title.setPadding(DisplayUtil.dpToPx(10, res), DisplayUtil.dpToPx(12, res), 0, DisplayUtil.dpToPx(6, res));
            title.setTextColor(res.getColor(R.color.gray20));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            title.setBackgroundColor(Color.WHITE);
            return new TitleHolder(title);
        } else {
            View v = LayoutInflater.from(C.get()).inflate(R.layout.complex_list_app_item, parent, false);
            v.setBackgroundResource(R.drawable.dialog_btn_background_selector);
            v.findViewById(R.id.lv_divider).setVisibility(View.GONE);
            return new AppItemViewHolder(v);
        }

    }

    public void update(MarketObservable observable, Object data) {
        if (data instanceof Pair) {
            Pair pair = (Pair) data;
            if (pair.first.equals(SessionManager.P_INSTALL_APP) || pair.first.equals(SessionManager.P_REMOVE_APP)) {
                notifyDataSetChanged();
            } else if (pair.first.equals(SessionManager.P_UPGRADE_NUM)) {
                notifyDataSetChanged();
            }
        } else if (data instanceof String) {
            if (SessionManager.ADD_OR_REMOVE_DOWNLOAD.equals(data)) {
                notifyDataSetChanged();
            } else if (SessionManager.DOWNLOAD_LOAD_COMPLETED.equals(data)) {
                notifyDataSetChanged();
            }
        }
    }

    static class TitleHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        public TitleHolder(View itemView) {
            super(itemView);
        }
    }
}
*/
