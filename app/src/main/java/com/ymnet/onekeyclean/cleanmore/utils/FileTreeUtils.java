package com.ymnet.onekeyclean.cleanmore.utils;

import android.text.TextUtils;

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
 * android market2345 wangdh@2345.com
 */
public class FileTreeUtils {

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
        // if I can delete directory then I know everything was deleted
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
        if (TextUtils.isEmpty(path))
            return false;
        File file = new File(path);
        if (file.isFile()) {
            //            return true;
            return file.delete();
        }
        return false;
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
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
