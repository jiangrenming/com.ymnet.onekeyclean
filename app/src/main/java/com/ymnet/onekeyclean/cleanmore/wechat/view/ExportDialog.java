package com.ymnet.onekeyclean.cleanmore.wechat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;


/**
 * Created by wangdh on 6/2/16.
 * gmail:wangduheng26@gamil.com
 */
public class ExportDialog extends Dialog {
    public static final int STYLE_SPINNER = 0;

    public static final int STYLE_HORIZONTAL = 1;
    private int mProgressStyle = STYLE_HORIZONTAL;
    private Handler mViewUpdateHandler;
    private CharSequence title;
    private int  index;
    private int mProgressVal;
    private boolean mHasStarted;
    private int mMax=100;
    TextView tv_title, tv_percentage, tv_progress;
    ProgressBar mProgress;

    public ExportDialog(Context context) {
        super(context);
    }

    public ExportDialog(Context context, int dialog) {
        super(context, dialog);
    }

    public static ExportDialog create(Context context, CharSequence title) {
        ExportDialog dialog = new ExportDialog(context, R.style.dialog);
        dialog.setTitle(title);
        dialog.setCancelable(false);
        return dialog;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.export_progress);
        tv_title = (TextView) findViewById(R.id.dialog_title);
        tv_title.setText(title);
        tv_percentage = (TextView) findViewById(R.id.tv_percentage);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        mProgress = (ProgressBar) findViewById(R.id.pb);
        if (mProgressStyle == STYLE_HORIZONTAL) {

            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    /* Update the number and percent */
                    int progress = mProgress.getProgress();
//                    int max = mProgress.getMax();
                    tv_percentage.setText(FormatUtils.percent(progress,mMax));
                    tv_progress.setText(String.valueOf(progress)+"/"+mMax);
                }
            };
            if (mMax > 0) {
                setMax(mMax);
            }
            if (mProgressVal > 0) {
                setProgress(mProgressVal);
            }

            onProgressChanged();

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }


    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title=title;
    }
}
