package com.ymnet.onekeyclean.cleanmore.utils;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;

import com.ymnet.onekeyclean.cleanmore.wechat.MTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * <获取文件夹或者文件大小>
     *
     * @param path path 路径或者文件
     * @return 以BKMG来计量
     * @throw
     */

    public static long getPathFileSize(String path) {
        File file = new File(path.trim());
        long fileSizes = 0;
        if (null != file && file.exists()) {
            if (file.isDirectory()) { // 如果路径是文件夹的时候
                fileSizes = getFileFolderTotalSize(file);
            } else if (file.isFile()) {
                fileSizes = file.length();
            }
        }
        return fileSizes;
    }


    public static long getFileFolderTotalSize(File fileDir) {
        long totalSize = 0;
        if (fileDir == null || !fileDir.exists()) {
            return 0;
        } else {
            if (fileDir.isFile()) {
                return fileDir.length();
            } else if (fileDir.isDirectory()) {
                File[] fileList = fileDir.listFiles();
                if (fileList != null) {
                    for (File f : fileList) {
                        totalSize += getFileFolderTotalSize(f);
                    }
                }
            }
        }
        return totalSize;
    }

    /**
     * 提前测量view的宽高
     *
     * @param view
     */
    public static void beforehandMeasuredViewHeight(View view) {
        if (view == null) {
            return;
        }
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
    }


    public static String formatFileSizeToPic(long fileSize) {// 转换文件大小
        String fileSizeString = "";
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        if (fileSize >= 1048576000) {
            fileSizeString = decimalFormat.format((double) fileSize / (1 * 1024 * 1024 * 1024)) + "GB";
        } else if (fileSize >= 1024000) {
            fileSizeString = decimalFormat.format((double) fileSize / (1 * 1024 * 1024)) + "MB";
        } else {
            fileSizeString = decimalFormat.format((double) fileSize / (1 * 1024)) + "KB";
        }
        return fileSizeString;
    }

    public static String getOutSDPath() {
        String outPath = null;
        ArrayList<String> list;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            list = ZhuShouUtils.getExtraSDCards(C.get());
        } else {
            list = (ArrayList<String>) getExtSDCardPaths();
        }


        if (list != null && list.size() > 0) {
            outPath = list.get(0);
        }
        String external = getSDPath();
        if (external != null && outPath != null && !external.equals(outPath)) {
            File file = new File(outPath);
            if (file.exists()) {
                return outPath;
            }
        }
        return outPath;
    }

    /*
     * 获得SD卡目录，默認內置的
     */
    public static String getSDPath() {

        boolean sdCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); // 判断sd卡是否存在
        if (sdCardExist) {
            return Environment.getExternalStorageDirectory().toString();// 获取跟目录
        } else {
            return null;
        }


    }

    public static List<String> getExtSDCardPaths() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.endsWith(Environment.MEDIA_UNMOUNTED) && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            paths.add(extFile.getAbsolutePath());
        }
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line.contains("storage"))
                        || line.contains("secure") || line.contains("asec") || line.contains("firmware")
                        || line.contains("shell") || line.contains("obb") || line.contains("legacy")
                        || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data") || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory() || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                paths.add(mountPath);
            }
            process.waitFor();
            process.destroy();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                isr.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return paths;
    }

    /**
     * 得到对应目录下子文件的数目
     *
     * @return
     */
    public static int getFileListCount(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        File f = new File(path);
        if (!f.exists()) {
            return 0;
        } else {
            if (f.isFile()) {
                return 0;
            } else {
                if (f != null) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        return files.length;
                    }
                }
                return 0;
            }
        }
    }

    /**
     * 得到sd根目录
     */
    public static String getRootPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "";
        }

    }

    public static void deleteNotContainFolder(File file) {
        try {
            if (file.exists())
            /*if (!file.exists()) {
            } else*/ {
                if (file.isFile()) {
                    file.delete();
                } else {
                    File[] files = file.listFiles();
                    if (files == null || files.length == 0) {
                        return;
                    }
                    for (File item : files) {
                        delete(item);
                    }
                }
            }
        } catch (Exception e) {

        }

    }

    /**
     * 若将整个文件夹删除，则只需调用这个方法
     */
    public static void delete(File file) {
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            delete(f);
                        }
                    }
                    file.delete();
                }
            }
        } catch (Exception e) {

        }

    }

    /**
     * 得到特定字体大小的样式
     *
     * @param text 需要改变的String
     * @return
     */
    public static SpannableString getSpannableString(String text) {
        SpannableString msp = new SpannableString(text);
        msp.setSpan(new AbsoluteSizeSpan(15, true), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(22, true), 4, text.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new AbsoluteSizeSpan(15, true), text.length() - 2, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return msp;
    }

    /**
     * * 复制assets中的文件到指定目录下 * @param context * @param assetsFileName * @param
     * targetPath * @return
     */
    public static boolean copyAssetData(Context context, String assetsFileName,
                                        String targetPath) {
        try {
            InputStream inputStream = context.getAssets().open(assetsFileName);
            FileOutputStream output = new FileOutputStream(targetPath
                    + File.separator + assetsFileName);
            byte[] buf = new byte[10240];
            int count = 0;
            while ((count = inputStream.read(buf)) > 0) {
                output.write(buf, 0, count);
            }
            inputStream.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 异步删除文件夹 不删除外层文件夹 如果file是文件直接删除 如果是文件夹则递归删除里面的所有文件和文件夹 但是不删除对外层的文件夹
     *
     * @param file
     * @return
     */
    public static void asynchronousDeleteFolderNoContainOuterFolder(final File file) {
        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (file != null && file.exists()) {
                    if (file.isFile()) {
                        file.delete();
                    } else {
                        File[] items = file.listFiles();
                        if (items != null) {
                            for (File item : items) {
                                delete(item);
                            }
                        }
                    }
                }
            }
        });

    }


    public static void asynchronousDeleteFolders(final List<String> paths) {
        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                for (String path : paths) {
                    if (!TextUtils.isEmpty(path)) {
                        File file = new File(path);
                        if (file != null && file.exists()) {
                            if (file.isFile()) {
                                file.delete();
                            } else {
                                File[] items = file.listFiles();
                                if (items != null) {
                                    for (File item : items) {
                                        delete(item);
                                    }
                                }
                                file.delete();
                            }
                        }
                    }

                }

            }
        });

    }

    /**
     * 得到现在运行activityd 类名
     *
     * @param context
     * @return
     */
    public static String getCurrentTopActivity(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String name = cn.getPackageName() + cn.getShortClassName();
            return name;
        } catch (Exception e) {
            return "";
        }
    }


    public static int getScreenHeight(Context context) {
        Class c;
        int y = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            y = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return y;
    }
}
