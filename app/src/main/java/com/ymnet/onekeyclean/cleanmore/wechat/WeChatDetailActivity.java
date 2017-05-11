package com.ymnet.onekeyclean.cleanmore.wechat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.constants.WeChatConstants;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.ToastUtil;
import com.ymnet.onekeyclean.cleanmore.wechat.adapter.WeChatExpandableAdapter;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ChangeMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ExportMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatPicMode;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.DetailPresImpl;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.WeChatDetailPresenter;
import com.ymnet.onekeyclean.cleanmore.wechat.view.ExportDialog;
import com.ymnet.onekeyclean.cleanmore.wechat.view.WeChatDetailMvpView;

import bolts.Task;



public class WeChatDetailActivity extends ImmersiveActivity implements WeChatDetailMvpView {
    private WeChatDetailPresenter     mPresenter;
    private ExpandableListView        mEvl;
    private TextView                  mBtn;
    private View                      mBtn_export;
    private BaseExpandableListAdapter adapter;
    public static final String EXPORT_TIP_POP = "tip";
    private TextView mTv_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat_item);
        int extra = getIntent().getIntExtra(WeChatActivity.EXTRA_ITEM_POSITION, -1);
        if (extra == -1) {
            this.finish();
            return;
        }
        mPresenter = new DetailPresImpl(this, extra);
        mBtn = (TextView) findViewById(R.id.btn_bottom_delete);
        mBtn_export = findViewById(R.id.btn_export);
        mEvl = (ExpandableListView) findViewById(R.id.elv);
        View vs = findViewById(R.id.vs_tip);
        WeChatPicMode type = (WeChatPicMode) mPresenter.getData();
        if (type == null || type.isEmpty()) {
            this.finish();
        } else {
            initTitleBar(type.getFileName());
            if (WeChatConstants.WECHAT_TYPE_VOICE == type.getType()) {
                vs.setVisibility(View.VISIBLE);
            } else {
                vs.setVisibility(View.GONE);
            }
            adapter = new WeChatExpandableAdapter(this, mPresenter, type);
            mEvl.setAdapter(adapter);
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveShow();
                    showDialogConfirmDelete(mPresenter.getSelectCount());
                }
            });
            if (WeChatConstants.WECHAT_TYPE_VOICE != type.getType()) {
                mBtn_export.setVisibility(View.VISIBLE);
                mBtn_export = findViewById(R.id.btn_export);
                mBtn_export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveShow();
                        if (mPresenter.checkStorage()) {
                            String message = mPresenter.checkExportFileLimit();
                            if (TextUtils.isEmpty(message)) {
                                showDialogConfirmExport();
                            } else {
                                showExceedLimit(message);
                            }
                        } else {
                            ToastUtil.showToastForLong(getString(R.string.item_space_error));
                        }

                    }
                });
                mBtn_export.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showTip();
                    }
                }, 500);
            } else {
                mBtn_export.setVisibility(View.GONE);
            }
            mEvl.expandGroup(0);
        }
    }

    PopupWindow popupWindow;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (dialogPb != null && dialogPb.isShowing()) {
            dialogPb.dismiss();
        }
    }

    private void showTip() {
        boolean hasShow = checkPopup();
        if (!hasShow) {
            int[] location = new int[2];
            mBtn_export.getLocationOnScreen(location);
            View view = View.inflate(C.get(), R.layout.wechat_export_tip_layout, null);
            ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);

            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(null);
            popupWindow.setClippingEnabled(false);
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveShow();
                }
            });
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupHeight = view.getMeasuredHeight();
            if (!isFinishing()) {
                popupWindow.showAtLocation(mBtn_export, Gravity.NO_GRAVITY, location[0], location[1] - popupHeight - 5);
            }

        }
    }

    /**
     * check pop 是否已经显示过
     *
     * @return
     */
    private boolean checkPopup() {
        return CleanSetSharedPreferences.getLastSet(C.get(), EXPORT_TIP_POP, false);
    }

    private void saveShow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        CleanSetSharedPreferences.setLastSet(C.get(), EXPORT_TIP_POP, true);
    }

    private void showDialogConfirmExport() {
        final int count = mPresenter.getSelectCount();
        dialog = DialogFactory.createDialog(this, R.layout.dialog_export);
        TextView tv_title = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView tv_message = (TextView) dialog.findViewById(R.id.dialog_message);
        mTv_path = (TextView) dialog.findViewById(R.id.dialog_path);
        Button btn1 = (Button) dialog.findViewById(R.id.dialog_btn1);
        tv_title.setText(getString(R.string.confir_export_count_file, count));
        tv_message.setText(Html.fromHtml(getString(R.string.wechat_export_explanation)));
        mTv_path.setText(R.string.wechat_export_path);
        btn1.setText(R.string.dialog_cancel);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        Button btn0 = (Button) dialog.findViewById(R.id.dialog_btn0);
        btn0.setText(R.string.begin_export);
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                mPresenter.export(count);
                //                StatisticSpec.sendEvent(StatisticEventContants.cleanwechat_export);
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    Dialog       dialog;
    Dialog       dialogPb;
    ExportDialog mExportDialog;
    Dialog       mCompleteDialog;
    Dialog       mExceedLimitDialog;

    private void showDialogConfirmDelete(int count) {//确认删除x个文件
        dialog = DialogFactory.createDialog(this, R.layout.dialog_filedelete, getString(R.string.confir_delete_count_file, count), "永久从设备中删除这些文件,删除后将不可恢复",
                getString(R.string.yes_zh), getString(R.string.no_zh),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        mPresenter.remove();
                        setResult();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();

                    }
                });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void setResult() {
        Intent intent = getIntent();
        intent.putExtra(WeChatActivity.FLAG_CHANGE, true);
        setResult(RESULT_OK, intent);
    }

    private void initTitleBar(String name) {
        View left_btn = findViewById(R.id.left_btn);
        TextView page_title = (TextView) findViewById(R.id.page_title);
        page_title.setText(name);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeChatDetailActivity.this.finish();
            }
        });
    }


    @Override
    public void setText(final String str) {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (mBtn != null) {
                    mBtn.setText(str);
                    if (getString(R.string.file_delete).equals(str)) {
                        mBtn.setEnabled(false);
                    } else {
                        mBtn.setEnabled(true);
                    }

                    int count = mPresenter == null ? 0 : mPresenter.getSelectCount();
                    mBtn_export.setEnabled(count > 0);
                }
            }
        });

    }

    @Override
    public void hideLoading() {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long size = mPresenter.getCount();
                    if (size == 0) {
                        WeChatDetailActivity.this.finish();
                    } else {
                        adapter.notifyDataSetChanged();
                        if (dialogPb != null && dialogPb.isShowing()) {
                            dialogPb.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void showLoading() {
        if (dialogPb == null) {
            dialogPb = DialogFactory.createDialog(this, R.layout.common_loading_dialog);
            dialogPb.setCancelable(false);
            dialogPb.setCanceledOnTouchOutside(false);
        }
        dialogPb.show();
    }


    @Override
    public void showExportDialog(final boolean show) {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    mExportDialog = DialogFactory.createExportDialog(WeChatDetailActivity.this, getString(R.string.exporting));
                    mExportDialog.setMax(mPresenter.getSelectCount());
                    mExportDialog.setCancelable(false);
                    mExportDialog.setCanceledOnTouchOutside(false);
                    mExportDialog.show();
                } else {
                    if (mExportDialog != null && mExportDialog.isShowing())
                        mExportDialog.dismiss();
                    mExportDialog = null;
                }
            }
        });


    }

    @Override
    public void changeExportProgress(final ChangeMode mode) {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (mExportDialog != null) {
                    mExportDialog.setProgress(mode.progress);
                }
            }
        });

    }

    @Override
    public void showExportComplete(final ExportMode mode) {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {


                mCompleteDialog = DialogFactory.createDialog(WeChatDetailActivity.this, R.layout.wechat_export_complete);

                TextView dialog_title = (TextView) mCompleteDialog.findViewById(R.id.dialog_title);
                TextView tv_current = (TextView) mCompleteDialog.findViewById(R.id.tv_current);
                Button dialog_btn0 = (Button) mCompleteDialog.findViewById(R.id.dialog_btn0);
                TextView dialog_path = (TextView) mCompleteDialog.findViewById(R.id.dialog_path);
                TextView dialog_fail_tip = (TextView) mCompleteDialog.findViewById(R.id.dialog_fail_tip);
                TextView dialog_exist_tip = (TextView) mCompleteDialog.findViewById(R.id.dialog_exist_tip);

                dialog_title.setText(getString(R.string.alert));
                if (mode.successCount == 0) {
                    tv_current.setVisibility(View.GONE);
                    dialog_path.setVisibility(View.GONE);
                } else {
                    dialog_path.setVisibility(View.VISIBLE);
                    tv_current.setText(Html.fromHtml(getString(R.string.wechat_export_success_tip, mode.successCount)));
                }
                if (mode.failCount == 0) {
                    dialog_fail_tip.setVisibility(View.GONE);
                } else {
                    dialog_fail_tip.setVisibility(View.VISIBLE);
                    dialog_fail_tip.setTextColor(getResources().getColor(R.color.wechat_export_fail_color));
                    dialog_fail_tip.setText(getString(R.string.wechat_export_error_tip, mode.failCount));
                }
                if (mode.hasExport > 0) {
                    dialog_exist_tip.setTextColor(getResources().getColor(R.color.wechat_export_fail_color));
                    dialog_exist_tip.setText(getString(R.string.wechat_export_exist_tip, mode.hasExport));
                    dialog_exist_tip.setVisibility(View.VISIBLE);
                } else {
                    dialog_exist_tip.setVisibility(View.GONE);
                }
                dialog_btn0.setText(R.string.i_know);
                dialog_btn0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCompleteDialog.dismiss();
                    }
                });
                mCompleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (adapter != null)
                            adapter.notifyDataSetChanged();

                    }
                });
                mCompleteDialog.show();
                //                StatisticSpec.sendEvent(StatisticEventContants.cleanwechat_export_finish);
            }
        });
    }

    @Override
    public void changeGroupCount() {
        if (mEvl != null) {
            Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    mEvl.expandGroup(0);
                    mEvl.setSelectedChild(0, 0, true);
                }
            });
        }
    }


    @Override
    public void showError() {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                WeChatDetailActivity.this.finish();
            }
        });
    }


    public void showExceedLimit(final String message) {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                mExceedLimitDialog = DialogFactory.createDialog(WeChatDetailActivity.this, R.layout.dialog_filedelete_single_button);
                ((TextView) mExceedLimitDialog.findViewById(R.id.dialog_title)).setText(R.string.alert);
                ((TextView) mExceedLimitDialog.findViewById(R.id.dialog_message)).setText(Html.fromHtml(message));
                Button btn01 = (Button) mExceedLimitDialog.findViewById(R.id.dialog_btn0);
                btn01.setText(R.string.qr_capture_tip_know);
                btn01.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExceedLimitDialog.cancel();
                    }
                });
                //
                mExceedLimitDialog.setCancelable(true);
                mExceedLimitDialog.setCanceledOnTouchOutside(true);
                mExceedLimitDialog.show();
            }
        });
    }
}
