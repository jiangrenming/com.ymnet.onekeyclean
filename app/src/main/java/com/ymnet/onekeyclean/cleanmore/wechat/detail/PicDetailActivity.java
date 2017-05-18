package com.ymnet.onekeyclean.cleanmore.wechat.detail;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.PhotoView;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;



public class PicDetailActivity extends BaseFragmentActivity {
    PhotoView photoview;
    View pb_loading;
    public static final String EXTRA_PATH = "extra_path";
    private Resources resources;
    private Rect outRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);
        Intent intent = getIntent();
        String path = intent.getStringExtra(EXTRA_PATH);
        if (TextUtils.isEmpty(path)) {
            this.finish();
            return;
        }
        outRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        resources = getResources();
        photoview = (PhotoView) findViewById(R.id.photoview);
        pb_loading = findViewById(R.id.pb_loading);
        showPhoto(photoview, path, pb_loading);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_UP){
            this.finish();
        }
        return super.onTouchEvent(event);
    }

    private void showPhoto(final PhotoView photoView, final String path, final View progressBar) {

        Glide.with(this).load("file://" + path).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(photoView);

    }
}
