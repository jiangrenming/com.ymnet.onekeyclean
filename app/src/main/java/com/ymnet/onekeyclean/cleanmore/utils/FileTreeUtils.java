package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by wangduheng26 on 1/4/16.
 */
public class FileTreeUtils {
    private static final String TAG = "FileTreeUtils";

    public static void syncDeleteContents(final File directory) {
        Task.call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                deleteContents(directory);
                return null;
            }
        }, Task.BACKGROUND_EXECUTOR);
    }

    public static void syncDeleteAll(final File file) {
        Task.call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                deleteAll(file);
                return null;
            }
        }, Task.BACKGROUND_EXECUTOR);
    }

    public static boolean deleteContents(File directory) {
        try {
            if (directory == null || !directory.exists())
                return false;
            if (directory.isFile())
                return deleteRecursively(directory);
            File[] files = directory.listFiles();
            boolean success = true;
            if (files != null) {
                for (File file : files) {
                    success &= deleteRecursively(file);
                }
            }
            return success;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Deletes the file and if it's a directory deletes also any content in it
     *
     * @param file a file or directory
     * @return true if the file/directory could be deleted
     */
    private static boolean deleteRecursively(File file) {
        if (isFileDirOver(file, 15)) {
            return false;
        }
        if (file.isDirectory()) {
            deleteContents(file);
        }
        // if I can delete directory then I know everything was deleted
        return file.delete();
    }


    public static boolean deleteAll(File file) {
        try {
            if (file == null || !file.exists())
                return false;
            if (file.isFile())
                return deleteRecursivelyFolder(file);
            File[] files = file.listFiles();
            if (files == null || files.length == 0)
                return file.delete();
            boolean success = true;
            for (File temp : files) {
                success &= deleteRecursivelyFolder(temp);
            }
            success &= file.delete();
            return success;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean deleteRecursivelyFolder(File file) {
        if (isFileDirOver(file, 15)) {
            return false;
        }
        if (file.isDirectory()) {
            deleteAll(file);
        }
        return file.delete();
    }

    public static boolean isFileDirOver(File file, int count) {
        boolean flag = false;
        if (file == null || !file.exists()) {
            return flag;
        }

        String path = file.getAbsolutePath();
        String[] split = path.split(File.separator);
        if (split != null && split.length > count) {
            flag = true;
        }

        return flag;
    }

    /**
     * 文件路径深度是否大于10（不包括sd卡根路径）
     *
     * @param file
     * @return
     */
    public static boolean isFileDirOver10(File file) {
        return isFileDirOver(file, 13);
    }

    public static boolean simpleDeleteFile(String path) {
        Log.d(TAG, "simpleDeleteFile: "+path);
        if (TextUtils.isEmpty(path)) {
            Log.d(TAG, "simpleDeleteFile: false,路径不存在");
            return false;
        }
        File file = new File(path);
        if (file.exists()){
            if (file.isFile()) {
                Log.d(TAG, "simpleDeleteFile: "+file.delete()+"删除");
                boolean delete = file.delete();
//                scanFileAsync(C.get(),path);
                return delete;
            }else {
                Log.i(TAG,"不是文件");
            }
        }else {
            Log.i(TAG,"文件不存在");
        }

        Log.d(TAG, "simpleDeleteFile: false,路径存在,但不是文件,没有删除");
        return false;
    }

    public static  void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }
    public static boolean copy(File source, File target) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }
}
