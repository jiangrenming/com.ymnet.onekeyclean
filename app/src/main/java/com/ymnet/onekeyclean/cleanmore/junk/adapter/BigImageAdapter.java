package com.ymnet.onekeyclean.cleanmore.junk.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.PhotoView;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;

import java.util.ArrayList;


public class BigImageAdapter extends PagerAdapter {
    private Context mContext;

    private ArrayList<FileInfo> mInfos;

    private ShowHideListerer listerer;

    public BigImageAdapter(Context context, ArrayList<FileInfo> infos) {
        this.mContext = context;
        this.mInfos = infos;
    }

    @Override
    public int getCount() {
        return mInfos == null ? 0 : mInfos.size();
    }

    public void setChangeListerer(ShowHideListerer listener) {
        this.listerer = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = View.inflate(mContext, R.layout.scroll_page_view, null);
        PhotoView photoView = (PhotoView) v.findViewById(R.id.photoview);
        //        View retryLayout = v.findViewById(R.id.retry_layout);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.loading);
        showPhoto(photoView, mInfos.get(position).filePath, progressBar);
        container.addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private void showPhoto(final PhotoView photoView, final String path, final ProgressBar progressBar) {
        Glide.with(mContext).load("file://" + path).fitCenter().listener(new RequestListener<String, GlideDrawable>() {
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


       /* ImageRequest request = ImageRequestBuilder.newBuilderWithSource(UriUtil.parseUriOrNull("file://" + path))
                .setResizeOptions(new ResizeOptions(rect.width(), rect.height()))
                .build();
        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(photoView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSubmit(String id, Object callerContext) {
                        super.onSubmit(id, callerContext);
                        progressBar.setVisibility(View.VISIBLE);

                    }
                })
                .build();
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(resources)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(resources.getDrawable(R.drawable.break_bg), ScalingUtils.ScaleType.FIT_CENTER)
                .setRetryImage(resources.getDrawable(R.drawable.break_bg), ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        photoView.setHierarchy(hierarchy);
        photoView.setController(controller);*/
        photoView.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (listerer != null) {
                                                 listerer.onChange();
                                             }
                                         }
                                     }

        );
    }

    public interface ShowHideListerer {
        void onChange();
    }
}
