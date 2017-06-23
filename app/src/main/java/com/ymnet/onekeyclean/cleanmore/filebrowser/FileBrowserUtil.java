/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class FileBrowserUtil {
    public static final String ROOT_PATH = "/";

    private static final String LOG_TAG = "FileBrowserUtil";

    /**
     * 文本类型集合
     */
    private static Map<String, String> textFileMap = new HashMap<String, String>();

    private static Map<String, String> picFileMap = new HashMap<String, String>();

    public static final HashMap<String, String> mDocMimeTypeMap = new HashMap<String, String>();

    public static final String PIC_DIR_PHOTO = "相机";

    public static final String PIC_DIR_SCREENSHOTS = "屏幕截图";

    public static final String PIC_DIR_QQ = "QQ接收图片";

    public static final String PIC_DIR_WX = "微信";

    static {
        /*
         * 文件分类规则：文档文件、多媒体文件、安装包和压缩包， 对于文档的文类除多媒体文件、安装包和
		 * 压缩包以外的其它的文件，系统能打开的分为文档文件，系统打不开的文件分类为其他 。
		 */

        // 文档文件
        textFileMap.put("txt", "txt");
        textFileMap.put("doc", "doc");
        textFileMap.put("docx", "docx");
        textFileMap.put("wps", "wps");
        textFileMap.put("xls", "xls");
        textFileMap.put("xlsx", "xlsx");
        textFileMap.put("ebk", "ekb");
        textFileMap.put("ebk3", "ekb3");
        textFileMap.put("htm", "htm");
        textFileMap.put("html", "html");
        textFileMap.put("ppt", "ppt");
        textFileMap.put("pptx", "pptx");
        textFileMap.put("pdf", "pdf");

        mDocMimeTypeMap.put("txt", "text/plain");
        mDocMimeTypeMap.put("doc", "application/msword");
        mDocMimeTypeMap.put("dot", "application/msword");
        mDocMimeTypeMap.put("wps", "application/kswps");
        mDocMimeTypeMap.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mDocMimeTypeMap.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        mDocMimeTypeMap.put("ppt", "application/vnd.ms-powerpoint");
        mDocMimeTypeMap.put("pps", "application/vnd.ms-powerpoint");
        mDocMimeTypeMap.put("pot", "application/vnd.ms-powerpoint");
        mDocMimeTypeMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        mDocMimeTypeMap.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        mDocMimeTypeMap.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        mDocMimeTypeMap.put("dps", "application/ksdps");
        mDocMimeTypeMap.put("xls", "application/vnd.ms-excel");
        mDocMimeTypeMap.put("xlt", "application/vnd.ms-excel");
        mDocMimeTypeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mDocMimeTypeMap.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        mDocMimeTypeMap.put("et", "application/kset");
        mDocMimeTypeMap.put("pdf", "application/pdf");
        mDocMimeTypeMap.put("ebk", "application/x-expandedbook");
        mDocMimeTypeMap.put("ebk3", "application/x-expandedbook");
        mDocMimeTypeMap.put("htm", "text/html");
        mDocMimeTypeMap.put("html", "text/html");
        mDocMimeTypeMap.put("xht", "application/xhtml+xml");
        mDocMimeTypeMap.put("xhtm", "application/xhtml+xml");
        mDocMimeTypeMap.put("xhtml", "application/xhtml+xml");

        picFileMap.put("jpg", "jpg");
        picFileMap.put("jpeg", "jpeg");
        picFileMap.put("png", "png");
        picFileMap.put("gif", "gif");
        picFileMap.put("tif", "tif");
        picFileMap.put("bmp", "bmp");
    }

    public static HashMap<String, String> getDocMimeTypeMap() {
        return mDocMimeTypeMap;
    }

    public static boolean isSDCardReady() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static FileInfo GetFileInfo(String filePath, int id) {
        File lFile = new File(filePath);
        if (!lFile.exists())
            return null;

        FileInfo lFileInfo = new FileInfo();
        lFileInfo.canRead = lFile.canRead();
        lFileInfo.canWrite = lFile.canWrite();
        lFileInfo.isHidden = lFile.isHidden();
        lFileInfo.fileName = FileBrowserUtil.getNameFromFilepath(filePath);
        lFileInfo.ModifiedDate = lFile.lastModified();
        lFileInfo.IsDir = lFile.isDirectory();
        lFileInfo.filePath = filePath;
        lFileInfo.fileSize = lFile.length();
        if (id != 0) {
            lFileInfo.fileId = id;
        }
        return lFileInfo;
    }


    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }

    public static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    public static boolean setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    public static boolean setText(View view, int id, int text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    // storage, G M K B
    public static String convertStorage(long size) {
        if (size >= ByteConstants.GB) {
            return String.format(Locale.CHINA, "%.1f GB", (float) size / ByteConstants.GB);
        } else if (size >= ByteConstants.MB) {
            float f = (float) size / ByteConstants.MB;
            return String.format(Locale.CHINA, f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= ByteConstants.KB) {
            float f = (float) size / ByteConstants.KB;
            return String.format(Locale.CHINA, f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format(Locale.CHINA, "%d B", size);
    }

    public static class SDCardInfo {
        public long total;

        public long free;
    }

    public static SDCardInfo getSDCardInfo() {
        String sDcString = Environment.getExternalStorageState();
        SDCardInfo result = new SDCardInfo();
        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            File pathFile = Environment.getExternalStorageDirectory();
            SDCardInfo info1 = getStatFsFromPath(pathFile.getPath());
            String outPath = Util.getOutSDPath();
            SDCardInfo info2 = getStatFsFromPath(outPath);
            result.total = info1.total + info2.total;
            result.free = info1.free + info2.free;
        }

        return result;
    }

    private static SDCardInfo getStatFsFromPath(String path) {
        SDCardInfo info = new SDCardInfo();
        if (TextUtils.isEmpty(path)) return info;
        try {
            android.os.StatFs statfs = new android.os.StatFs(path);

            // 获取SDCard上BLOCK总数
            long nTotalBlocks = statfs.getBlockCount();

            // 获取SDCard上每个block的SIZE
            long nBlocSize = statfs.getBlockSize();

            // 获取可供程序使用的Block的数量
            long nAvailaBlock = statfs.getAvailableBlocks();

            // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
//				long nFreeBlock = statfs.getFreeBlocks();


            // 计算SDCard 总容量大小MB
            info.total = nTotalBlocks * nBlocSize;

            // 计算 SDCard 剩余大小MB
            info.free = nAvailaBlock * nBlocSize;


        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.toString());
        }
        return info;
    }

    public static final HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
        private static final long serialVersionUID = 7779166435567287593L;

        {
            add("text/plain");
            add("application/mspowerpoint");
            add("application/msexcel");
            add("application/pdf");
            add("application/msword");
            add("text/html");
            add("application/vnd.ms-excel");
            add("application/x-expandedbook");

        }
    };

    public static final HashSet<String> sZipFileMimeType = new HashSet<String>() {
        private static final long serialVersionUID = -2222580660231413163L;

        {
            add("application/zip");
            add("application/x-rar-compressed");
        }
    };


    public static String formatDuration(int duration) {
        String s;
        String m;
        String h;
        int second = duration / 1000;
        int minute = second / 60;
        int hour = minute / 60;

        s = changeFor60(second);
        m = changeFor60(minute);
        if (hour > 0) {
            h = changeFor60(hour);
            return h + ":" + m + ":" + s;
        } else {
            return m + ":" + s;
        }
    }

    private static String changeFor60(int second) {
        int temp = second % 60;
        if (temp == 0) {
            return "00";
        } else if (temp < 10) {
            return "0" + temp;
        } else {
            return "" + temp;
        }
    }

    public static int setWallPaper(Context context, InputStream data, Bitmap bmp, boolean isBmp) {
        if (!isBmp && data == null) {
            return -1;
        }

        if (isBmp && bmp == null) {
            return -1;
        }

        try {
            if (isBmp) {
                WallpaperManager.getInstance(context).setBitmap(bmp);
            } else {
                WallpaperManager.getInstance(context).setStream(data);
            }

            return 1;
        } catch (Throwable e) {
            e.printStackTrace();

            return -1;
        } finally {
            try {
                if (isBmp) {
                    bmp.recycle();
                } else {
                    data.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void openFile(Context context, FileInfo info) {
        switch (info.fc) {
            case Doc:
                FileBrowserUtil.openFile(context,info.filePath, info.mimeType);
                break;
            case Picture:
                break;
            case Music:
                FileBrowserUtil.openFile(context,info.filePath, info.mimeType);
                break;
            case Video:
                FileBrowserUtil.openFile(context,info.filePath, info.mimeType);
                break;
            case Apk:
                File file = new File(info.filePath);
                Intent i = new Intent();
                String uriStr = "file://" + file.getAbsolutePath();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(uriStr), "application/vnd.android.package-archive");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (!context.getPackageManager().queryIntentActivities(i, 0).isEmpty()) {
                    try {
                        context.startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Zip:
                FileBrowserUtil.openFile(context,info.filePath, info.mimeType);
                break;
        }
    }

    public static void openFile(Context context, String path, String type) {
        try {
            File file = new File(path);
            if (file == null || !file.exists()) {
                Toast.makeText(context, "打开失败，文件路径不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, type);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "打开失败，不支持此格式", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteImage(Context context, int id, String path, FileCategoryHelper.FileCategory fc) {
        boolean result = deleteFile(context, path, fc);

        if (result && id > 0) {
            context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Images.ImageColumns._ID + " = " + id, null);
        }

        return result;
    }

    public static boolean deleteMusic(Context context, int id, String path,FileCategoryHelper.FileCategory fc) {
        boolean result = deleteFile(context, path, fc);

        if (result && id > 0) {
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Audio.AudioColumns._ID + " = " + id, null);
        }

        return result;
    }

    public static boolean deleteVideo(Context context, int id, String path, FileCategoryHelper.FileCategory fc) {
        boolean result = deleteFile(context, path, fc);

        if (result && id > 0) {
            context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.Video.VideoColumns._ID + " = " + id, null);
        }

        return result;
    }

    @SuppressLint("NewApi")
    public static boolean deleteOtherFile(Context context, int id, String path, FileCategoryHelper.FileCategory fc) {
        boolean result = deleteFile(context, path, fc);

        if (result && id > 0) {
            if (Build.VERSION.SDK_INT > 10) {
                context.getContentResolver().delete(MediaStore.Files.getContentUri("external"),
                        MediaStore.Files.FileColumns._ID + " = " + id, null);
            }
        }

        return result;
    }

    public static boolean deleteFile(Context context, String path, FileCategoryHelper.FileCategory fc) {
        boolean result = false;
        long size = 0;
        if (TextUtils.isEmpty(path)) {
            FileControl.getInstance(context).notifyDataChange(fc, 1, size);
            return false;
        }

        try {
            File file = new File(path);

            if (file.exists()) {
                if (!file.canWrite()) {
                    String[] args1 = {"chmod", "771", file.getPath().substring(0, file.getPath().lastIndexOf("/"))};
                    Process p1 = Runtime.getRuntime().exec(args1);
                    p1.waitFor();
                    p1.destroy();
                    String[] args2 = {"chmod", "777", file.getPath()};
                    Process p2 = Runtime.getRuntime().exec(args2);
                    p2.waitFor();
                    p2.destroy();
                }

                size = file.length();
                result = file.delete();

                if (result) {
                    FileControl.getInstance(context).notifyDataChange(fc, 1, size);
                }
            } else {
                FileControl.getInstance(context).notifyDataChange(fc, 1, size);
                result = true;
            }
        } catch (Exception e) {
        }

        return result;
    }

    public static void getApkInfo(Context context, FileInfo info) {
        info.appName = info.fileName;

        if (context == null || info == null || TextUtils.isEmpty(info.filePath) || !info.filePath.endsWith("apk")) {
            return;
        }

        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return;
        }

        PackageInfo pkgInfo = pm.getPackageArchiveInfo(info.filePath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo == null) {
            return;
        }

        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        appInfo.sourceDir = info.filePath;
        appInfo.publicSourceDir = info.filePath;

        info.appName = pm.getApplicationLabel(appInfo).toString();
        info.drawable = appInfo.loadIcon(pm);
    }

   /* public static DownloadAppInfo getApkInfo(Context context, String apkPath) {

        if (context != null && !TextUtils.isEmpty(apkPath) && apkPath.endsWith("apk")) {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                PackageInfo pkgInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
                if (pkgInfo != null) {
                    ApplicationInfo appInfo = pkgInfo.applicationInfo;
                    appInfo.sourceDir = apkPath;
                    appInfo.publicSourceDir = apkPath;

                    DownloadAppInfo downloadAppInfo = new DownloadAppInfo();
                    downloadAppInfo.packageName = pkgInfo.packageName;
                    downloadAppInfo.appName = pm.getApplicationLabel(appInfo).toString();
                    downloadAppInfo.appIcon = appInfo.loadIcon(pm);
                    return downloadAppInfo;
                }
            }
        }
        return null;
    }*/

    /**
     * 获取文件的后缀名
     *
     * @param fileName 文件名
     * @return String 文件的后缀名
     */
    public static String getSuffix(String fileName) {
        String suffix = "";
        int lastDot = fileName.lastIndexOf(".");

        if (lastDot != -1 && lastDot != 0 && lastDot != fileName.length() - 1) {
            suffix = fileName.substring(lastDot + 1, fileName.length()).toLowerCase(Locale.getDefault());
        }

        return suffix;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 目标字符串
     * @return true:非空 false:空
     */
    public static boolean isNotEmpty(String str) {
        if (str != null && str.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否是文本文件
     *
     * @param suffix 文件后缀名
     * @return true:是 false:否
     */
    public static boolean isText(String suffix) {
        boolean isText = false;

        if (isNotEmpty(suffix)) {
            isText = textFileMap.containsKey(suffix.toLowerCase(Locale.CHINA));
        }

        return isText;
    }

    public static boolean isPic(String suffix) {
        boolean isPic = false;
        if (isNotEmpty(suffix)) {
            isPic = picFileMap.containsKey(suffix.toLowerCase(Locale.CHINA));
        }

        return isPic;
    }

    public static int changeWallPapger(Context context, InputStream wallpaper, boolean notify) {
        try {
            WallpaperManager.getInstance(context).setStream(wallpaper);
            try {
                if (notify) {
                    Toast.makeText(context, "设置壁纸成功", Toast.LENGTH_SHORT).show();
                }
                wallpaper.close();
                return 1;
            } catch (Exception e) {
                return 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Toast.makeText(context, "设置壁纸失败", Toast.LENGTH_SHORT).show();
                return 0;
            } catch (Exception e1) {
                return 0;
            }
        }
    }

}
