package com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.utils.DateFormatUtils;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.filebrowser.lazyload.ImageLoader;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.FileManagerInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/23.
 */

public class FileItemAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<FileInfo> mInfos;
    private Map<Integer, FileInfo> mDeleteMap;
    private OnCheckChangedListener onCheckChangedListener;
    private OnItemClickListener itemClickListener;
    private HashMap<String, Integer> mIconResIDMap;
    private HashMap<String, Integer> mTypeMap;
    private int imageWidth;
    private int width;
    private float denity;

    public FileItemAdapter(Context context, ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
        this.mContext = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mInfos = infos;
        this.mDeleteMap = deleteMap;
        denity = context.getResources().getDisplayMetrics().density;
        width = context.getResources().getDisplayMetrics().widthPixels;

        mIconResIDMap = new HashMap<String, Integer>();
        mIconResIDMap.put("doc", R.drawable.file_management_word_type_doc);
        mIconResIDMap.put("dot", R.drawable.file_management_word_type_doc);
        mIconResIDMap.put("wps", R.drawable.file_management_word_type_wps);
        mIconResIDMap.put("docx", R.drawable.file_management_word_type_doc);
        mIconResIDMap.put("dotx", R.drawable.file_management_word_type_doc);
        mIconResIDMap.put("ppt", R.drawable.file_management_word_type_ppt);
        mIconResIDMap.put("pps", R.drawable.file_management_word_type_ppt);
        mIconResIDMap.put("pos", R.drawable.file_management_word_type_ppt);
        mIconResIDMap.put("pptx", R.drawable.file_management_word_type_ppt);
        mIconResIDMap.put("ppsx", R.drawable.file_management_word_type_ppt);
        mIconResIDMap.put("potx", R.drawable.file_management_word_type_ppt);
        mIconResIDMap.put("dps", R.drawable.file_management_word_type_ppt);
        mIconResIDMap.put("xls", R.drawable.file_management_word_type_xls);
        mIconResIDMap.put("xlt", R.drawable.file_management_word_type_xls);
        mIconResIDMap.put("xlsx", R.drawable.file_management_word_type_xls);
        mIconResIDMap.put("xltx", R.drawable.file_management_word_type_xls);
        mIconResIDMap.put("et", R.drawable.file_management_word_type_xls);
        mIconResIDMap.put("pdf", R.drawable.file_management_word_type_pdf);
        mIconResIDMap.put("txt", R.drawable.file_management_word_type_txt);
        mIconResIDMap.put("ebk", R.drawable.file_management_word_type_ebk3);
        mIconResIDMap.put("ebk3", R.drawable.file_management_word_type_ebk3);
        mIconResIDMap.put("htm", R.drawable.file_management_word_type_html);
        mIconResIDMap.put("html", R.drawable.file_management_word_type_html);
        mIconResIDMap.put("xht", R.drawable.file_management_word_type_html);
        mIconResIDMap.put("xhtm", R.drawable.file_management_word_type_html);
        mIconResIDMap.put("xhtml", R.drawable.file_management_word_type_html);

        mTypeMap = new HashMap<String, Integer>();
        mTypeMap.put("7z", R.drawable.file_management_compress_type_7zip);
        mTypeMap.put("zip", R.drawable.file_management_compress_type_zip);
        mTypeMap.put("rar", R.drawable.file_management_compress_type_rar);
        mTypeMap.put("iso", R.drawable.file_management_compress_type_iso);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case 0:
                view = View.inflate(mContext, R.layout.documents_item, null);
                holder = new DocumentsHolder(view);
                break;
            case 1:
                imageWidth = (int) ((width - (FileManagerInfo.SPACE * denity * 4)) / 3);
                view = View.inflate(mContext, R.layout.pictures_item, null);
                holder = new PicturesHolder(view);
                break;
            case 2:
                view = View.inflate(mContext, R.layout.musics_item, null);
                holder = new MusicsHolder(view);
                break;
            case 3:
                view = View.inflate(mContext, R.layout.videos_item, null);
                holder = new VideosHolder(view);
                break;
            case 4:
                view = View.inflate(mContext, R.layout.apks_item, null);
                holder = new ApksHolder(view);
                break;
            case 5:
                view = View.inflate(mContext, R.layout.zips_item, null);
                holder = new ZipsHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DocumentsHolder) {
            DocumentsHolder mHolder = (DocumentsHolder) holder;
            mHolder.docChecked.setChecked(mDeleteMap.containsKey(position));
            mHolder.docChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    int position = (Integer) cb.getTag();
                    if (cb.isChecked()) {
                        mDeleteMap.put(position, mInfos.get(position));
                    } else {
                        mDeleteMap.remove(position);
                    }

                    if (onCheckChangedListener != null) {
                        onCheckChangedListener.checkChanged();
                    }
                }
            });
            mHolder.docChecked.setTag(position);

            mHolder.docName.setText(mInfos.get(position).fileName);
            mHolder.docSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
            mHolder.docTime.setText(DateFormatUtils.format(mInfos.get(position).ModifiedDate, DateFormatUtils.PATTERN_YMDHM2));

            setDocPic(mHolder.docPic, mInfos.get(position).fileName);

            if (TextUtils.isEmpty(mInfos.get(position).mimeType)) {
                mInfos.get(position).mimeType = getMimeType(mInfos.get(position).fileName);
            }
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, position);
                }
            });
            mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemClickListener.onLongClick(view, position);
                }
            });
        } else if (holder instanceof PicturesHolder) {
            PicturesHolder mHolder = (PicturesHolder) holder;
            mHolder.pictureChecked.setChecked(mDeleteMap.containsKey(position));
            mHolder.pictureChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    int position = (Integer) cb.getTag();
                    if (cb.isChecked()) {
                        mDeleteMap.put(position, mInfos.get(position));
                    } else {
                        mDeleteMap.remove(position);
                    }

                    if (onCheckChangedListener != null) {
                        onCheckChangedListener.checkChanged();
                    }
                }
            });
            mHolder.pictureChecked.setTag(position);

            Glide.with(mContext)
                    .load("file://" + mInfos.get(position).filePath)
                    .into(mHolder.picturePic);

            if (TextUtils.isEmpty(mInfos.get(position).mimeType)) {
                mInfos.get(position).mimeType = getMimeType(mInfos.get(position).fileName);
            }
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, position);
                }
            });
            mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemClickListener.onLongClick(view, position);
                }
            });
        } else if (holder instanceof MusicsHolder) {
            MusicsHolder mHolder = (MusicsHolder) holder;
            mHolder.musicChecked.setChecked(mDeleteMap.containsKey(position));
            mHolder.musicChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    int position = (Integer) cb.getTag();
                    if (cb.isChecked()) {
                        mDeleteMap.put(position, mInfos.get(position));
                    } else {
                        mDeleteMap.remove(position);
                    }

                    if (onCheckChangedListener != null) {
                        onCheckChangedListener.checkChanged();
                    }
                }
            });
            mHolder.musicChecked.setTag(position);
            mHolder.musicName.setText(mInfos.get(position).fileName);
            mHolder.musicSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
            SpannableStringBuilder duration = changeDurationStyle(-1, mInfos.get(position).duration);
            mHolder.musicDuration.setText(duration);

            if (TextUtils.isEmpty(mInfos.get(position).mimeType)) {
                mInfos.get(position).mimeType = getMimeType(mInfos.get(position).fileName);
            }
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, position);
                }
            });
            mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemClickListener.onLongClick(view, position);
                }
            });
        } else if (holder instanceof VideosHolder) {
            VideosHolder mHolder = (VideosHolder) holder;
            mHolder.videoChecked.setChecked(mDeleteMap.containsKey(position));
            mHolder.videoChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    int position = (Integer) cb.getTag();
                    if (cb.isChecked()) {
                        mDeleteMap.put(position, mInfos.get(position));
                    } else {
                        mDeleteMap.remove(position);
                    }

                    if (onCheckChangedListener != null) {
                        onCheckChangedListener.checkChanged();
                    }
                }
            });
            mHolder.videoChecked.setTag(position);
            ImageLoader.getInstance(mContext).DisplayImage(mInfos.get(position).fileId, mHolder.videoPic,
                    R.drawable.screenshot, R.drawable.screenshot, -1, -1, ImageLoader.MEDIATYPE.VIDEO);
            mHolder.videoName.setText(mInfos.get(position).fileName);
            mHolder.videoSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
            SpannableStringBuilder duration = changeDurationStyle(-1, mInfos.get(position).duration);
            mHolder.videoDuration.setText(duration);

            if (TextUtils.isEmpty(mInfos.get(position).mimeType)) {
                mInfos.get(position).mimeType = getMimeType(mInfos.get(position).fileName);
            }
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, position);
                }
            });
            mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemClickListener.onLongClick(view, position);
                }
            });
        } else if (holder instanceof ApksHolder) {
            ApksHolder mHolder = (ApksHolder) holder;
            mHolder.apkChecked.setChecked(mDeleteMap.containsKey(position));
            mHolder.apkChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    int position = (Integer) cb.getTag();
                    if (cb.isChecked()) {
                        mDeleteMap.put(position, mInfos.get(position));
                    } else {
                        mDeleteMap.remove(position);
                    }

                    if (onCheckChangedListener != null) {
                        onCheckChangedListener.checkChanged();
                    }
                }
            });
            mHolder.apkChecked.setTag(position);
            mHolder.apkSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
            mHolder.apkDuration.setText(DateFormatUtils.format(mInfos.get(position).ModifiedDate, DateFormatUtils.PATTERN_YMDHM2));
            if (TextUtils.isEmpty(mInfos.get(position).appName)) {
                FileBrowserUtil.getApkInfo(mContext, mInfos.get(position));
            }
            mHolder.apkName.setText(mInfos.get(position).appName);
            mHolder.apkPic.setImageDrawable(mInfos.get(position).drawable);
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, position);
                }
            });
            mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemClickListener.onLongClick(view, position);
                }
            });
        } else if (holder instanceof ZipsHolder) {
            ZipsHolder mHolder = (ZipsHolder) holder;
            mHolder.zipChecked.setChecked(mDeleteMap.containsKey(position));
            mHolder.zipChecked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    int position = (Integer) cb.getTag();
                    if (cb.isChecked()) {
                        mDeleteMap.put(position, mInfos.get(position));
                    } else {
                        mDeleteMap.remove(position);
                    }

                    if (onCheckChangedListener != null) {
                        onCheckChangedListener.checkChanged();
                    }
                }
            });
            mHolder.zipChecked.setTag(position);

            mHolder.zipName.setText(mInfos.get(position).fileName);
            mHolder.zipSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
            mHolder.zipTime.setText(DateFormatUtils.format(mInfos.get(position).ModifiedDate, DateFormatUtils.PATTERN_YMDHM2));

            setZipPic(mHolder.zipPic, mInfos.get(position).fileName);
            mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(view, position);
                }
            });
            mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return itemClickListener.onLongClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        switch (mInfos.get(position).fc) {
            case Doc:
                type = 0;
                break;
            case Picture:
                type = 1;
                break;
            case Music:
                type = 2;
                break;
            case Video:
                type = 3;
                break;
            case Apk:
                type = 4;
                break;
            case Zip:
                type = 5;
                break;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        return mInfos.size();
    }

    public void setDate(ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
        this.mInfos = infos;
        this.mDeleteMap = deleteMap;
        notifyDataSetChanged();
    }

    private class DocumentsHolder extends RecyclerView.ViewHolder {
        private CheckBox docChecked;
        private ImageView docPic;
        private TextView docName;
        private TextView docSize;
        private TextView docTime;

        public DocumentsHolder(final View view) {
            super(view);
            docChecked = (CheckBox) view.findViewById(R.id.doc_checked);
            docPic = (ImageView) view.findViewById(R.id.doc_pic);
            docName = (TextView) view.findViewById(R.id.doc_name);
            docSize = (TextView) view.findViewById(R.id.doc_size);
            docTime = (TextView) view.findViewById(R.id.doc_time);
        }
    }

    private class MusicsHolder extends RecyclerView.ViewHolder {
        private CheckBox musicChecked;
        private ImageView musicPic;
        private TextView musicName;
        private TextView musicSize;
        private TextView musicDuration;

        public MusicsHolder(final View view) {
            super(view);
            musicChecked = (CheckBox) view.findViewById(R.id.music_checked);
            musicPic = (ImageView) view.findViewById(R.id.music_pic);
            musicName = (TextView) view.findViewById(R.id.music_name);
            musicSize = (TextView) view.findViewById(R.id.music_size);
            musicDuration = (TextView) view.findViewById(R.id.music_duration);
        }
    }

    private class PicturesHolder extends RecyclerView.ViewHolder {
        private FrameLayout frameLayout;
        private CheckBox pictureChecked;
        private ImageView picturePic;

        public PicturesHolder(final View view) {
            super(view);
            frameLayout = (FrameLayout) view.findViewById(R.id.picture_layout);
            pictureChecked = (CheckBox) view.findViewById(R.id.picture_checked);
            picturePic = (ImageView) view.findViewById(R.id.picture_img);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, imageWidth);
            frameLayout.setLayoutParams(params);
        }
    }

    private class VideosHolder extends RecyclerView.ViewHolder {
        private CheckBox videoChecked;
        private ImageView videoPic;
        private TextView videoName;
        private TextView videoSize;
        private TextView videoDuration;

        public VideosHolder(final View view) {
            super(view);
            videoChecked = (CheckBox) view.findViewById(R.id.video_checked);
            videoPic = (ImageView) view.findViewById(R.id.video_pic);
            videoName = (TextView) view.findViewById(R.id.video_name);
            videoSize = (TextView) view.findViewById(R.id.video_size);
            videoDuration = (TextView) view.findViewById(R.id.video_duration);
        }
    }

    private class ZipsHolder extends RecyclerView.ViewHolder {
        private CheckBox zipChecked;
        private ImageView zipPic;
        private TextView zipName;
        private TextView zipSize;
        private TextView zipTime;

        public ZipsHolder(final View view) {
            super(view);
            zipChecked = (CheckBox) view.findViewById(R.id.zip_checked);
            zipPic = (ImageView) view.findViewById(R.id.zip_pic);
            zipName = (TextView) view.findViewById(R.id.zip_name);
            zipSize = (TextView) view.findViewById(R.id.zip_size);
            zipTime = (TextView) view.findViewById(R.id.zip_time);
        }
    }

    private class ApksHolder extends RecyclerView.ViewHolder {
        private CheckBox apkChecked;

        private ImageView apkPic;

        private TextView apkName;

        private TextView apkSize;

        private TextView apkDuration;

        public ApksHolder(final View view) {
            super(view);
            apkChecked = (CheckBox) view.findViewById(R.id.apk_checked);
            apkPic = (ImageView) view.findViewById(R.id.apk_pic);
            apkName = (TextView) view.findViewById(R.id.apk_name);
            apkSize = (TextView) view.findViewById(R.id.apk_size);
            apkDuration = (TextView) view.findViewById(R.id.apk_duration);
        }
    }

    private void setDocPic(ImageView ivPic, String name) {
        String suffix = getSuffix(name);
        if (TextUtils.isEmpty(suffix)) {
            ivPic.setImageResource(R.drawable.file_management_word_type_txt);
            return;
        }

        Integer resID = mIconResIDMap.get(suffix.toLowerCase(Locale.CHINA));
        if (resID != null) {
            ivPic.setImageResource(resID);
        } else {
            ivPic.setImageResource(R.drawable.file_management_word_type_txt);
        }
    }

    private void setZipPic(ImageView ivPic, String name) {
        try {
            String suffix = name.substring(name.lastIndexOf(".") + 1);
            Integer resID = mTypeMap.get(suffix.toLowerCase(Locale.CHINA));
            if (resID != null) {
                ivPic.setImageResource(resID);
            } else {
                ivPic.setImageResource(R.drawable.file_management_compress_type_rar);
            }
        } catch (Exception e) {
            ivPic.setImageResource(R.drawable.file_management_compress_type_rar);
        }
    }

    public String getMimeType(String name) {
        String suffix = getSuffix(name);
        if (TextUtils.isEmpty(suffix)) {
            return "*/*";
        }

        String mimeType = FileBrowserUtil.getDocMimeTypeMap().get(suffix.toLowerCase(Locale.CHINA));
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = "*/*";
        }

        return mimeType;
    }

    private String getSuffix(String name) {
        String suffix = null;
        if (TextUtils.isEmpty(name)) {
            return suffix;
        }

        int index = name.lastIndexOf(".");
        if (index == -1 || index == name.length() - 1) {
            return suffix;
        }

        suffix = name.substring(index + 1);
        return suffix;
    }

    private SpannableStringBuilder changeDurationStyle(int currentDuration, int duration) {
        if (currentDuration > 0) {
            String current = FileBrowserUtil.formatDuration(currentDuration);
            String total = FileBrowserUtil.formatDuration(duration);
            SpannableStringBuilder builder = new SpannableStringBuilder(current + "/" + total);
            builder.setSpan(new ForegroundColorSpan(Color.RED), 0, current.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            return builder;
        } else {
            // notifyDataSetChanged();
            return new SpannableStringBuilder(FileBrowserUtil.formatDuration(duration));
        }
    }

    public void clear() {
        if (mIconResIDMap != null) {
            mIconResIDMap.clear();
            mIconResIDMap = null;
        }
        if (mTypeMap != null) {
            mTypeMap.clear();
            mTypeMap = null;
        }
    }

    public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
        this.onCheckChangedListener = onCheckChangedListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnCheckChangedListener {
        void checkChanged();
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);

        boolean onLongClick(View view, int position);
    }


}
