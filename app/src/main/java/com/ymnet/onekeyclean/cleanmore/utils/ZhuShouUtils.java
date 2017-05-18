package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZhuShouUtils {

    private static final String TAG = ZhuShouUtils.class.getSimpleName();

    private static WakeLock sBrightWakeLock;

    private static WakeLock sDimWakeLock;

    /*public static ConcurrentHashMap<String, InstalledApp> getAllAppInfo(Context context) {
        return DataCenterObserver.get(context).getInstalledApps();
    }

    public static void getCanMovedFeild(Context context) {
        ConcurrentHashMap<String, InstalledApp> Infos = getAllAppInfo(context);
        Collection<InstalledApp> apps = Infos.values();
        PackageManager pm = context.getPackageManager();

        for (InstalledApp a : apps) {
            if (!a.isCanMoveGeted) {
                int canMove = 0;
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(a.packageName, 0);
                    Field field = packageInfo.getClass().getField("installLocation");
                    field.setAccessible(true);
                    canMove = field.getInt(packageInfo);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                a.canMove = canMove;
                a.isCanMoveGeted = true;
            }
        }
    }*/

    /**
     * 获取所有已经安装的软件
     *
     * @param context
     * @return
     */
    public static int getAllAppNumber(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        int number = 0;
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                    && !applicationInfo.packageName.equals("com.ymnet")) {
                number++;
            }
        }

        return number;
    }

    /**
     * 将Drawable转换成Bitmap，然后就可以将Drawable对象通过Android的SK库存成一个字节输出流，
     * 最终还可以保存成为jpg和png的文件
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {

        BitmapDrawable bd = (BitmapDrawable) drawable;
        if (bd != null) {
            return bd.getBitmap();
            // Bitmap b;
            // b.getByteCount();
        } else {
            return null;
        }
    }

    public static byte[] zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(data.length);
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

    private static String bitmapToString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        long tt = System.currentTimeMillis();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        long tt01 = System.currentTimeMillis();
        Log.i("iconcompress", "" + Thread.currentThread().getName() + ":" + (tt01 - tt));
        byte[] bytes = bStream.toByteArray();
        tt01 = System.currentTimeMillis();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        long tt02 = System.currentTimeMillis();

        Log.i("base", "" + Thread.currentThread().getName() + ":" + (tt02 - tt01));
        try {
            bStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return string;
    }

    /**
     * Bitmap to string
     *
     * @param bitmap
     * @param out
     * @return
     */
    private static String bitmapToString(Bitmap bitmap, FileOutputStream out) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        long tt = System.currentTimeMillis();
        Bitmap b = bitmap.copy(Bitmap.Config.RGB_565, false);
        b.compress(CompressFormat.JPEG, 100, bStream);
        long tt01 = System.currentTimeMillis();
        Log.i("iconcompress", "" + Thread.currentThread().getName() + ":" + (tt01 - tt));
        try {
            int t = (int) (tt01 - tt);
            out.write(String.valueOf(t).getBytes());
            out.write("    ".getBytes());
            out.write("    ".getBytes());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        byte[] bytes = bStream.toByteArray();
        tt01 = System.currentTimeMillis();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        long tt02 = System.currentTimeMillis();

        try {
            int t = (int) (tt02 - tt01);
            out.write(String.valueOf(t).getBytes());
            out.write("    ".getBytes());
            out.write("    ".getBytes());
            t = bytes.length;
            out.write(String.valueOf(t).getBytes());
            out.write("    ".getBytes());
            out.write("    ".getBytes());
            t = string.getBytes().length;
            out.write(String.valueOf(t).getBytes());
            out.write("\r\n".getBytes());
            out.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Log.i("base", "" + Thread.currentThread().getName() + ":" + (tt02 - tt01));
        try {
            bStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return string;
    }

    /**
     * 将Drawable变成可以IO输出的字符串
     *
     * @param drawable
     * @param out
     * @return
     */
    public static String drawableToIOString(Drawable drawable, FileOutputStream out) {
        // long tt=System.currentTimeMillis();
        if (drawable == null) {
            return null;
        } else {
            String result = null;
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();
            result = bitmapToString(bitmap, out);
            return result;
        }
    }

    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // 内存剩余空间
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    // 内存总空间
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    // SD卡剩余空间
    public static long getAvailableExternalMemorySize() {
        if (isSDCardExist()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            long fstab = -1;
            try {
                File mountFile = new File("/etc/vold.fstab");
                if (mountFile.exists()) {
                    Scanner scanner = new Scanner(mountFile);
                    try {
                        while (scanner.hasNext()) {
                            String line = scanner.nextLine();
                            if (line.startsWith("dev_mount")) {
                                String[] lineElements = line.split(" ");
                                if (lineElements.length > 3) {
                                    String element = lineElements[1];
                                    if (element.equals("sdcard")) {
                                        File f = new File(lineElements[2]);
                                        if (null != f.listFiles()) {
                                            StatFs stat = new StatFs(f.getPath());
                                            long blockSize = stat.getBlockSize();
                                            long totalBlocks = stat.getBlockCount();
                                            return totalBlocks * blockSize;
                                        }
                                    }
                                }
                            }
                        }
                    } finally {
                        scanner.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fstab;
        }
    }

    // SD卡总空间
    public static long getTotalExternalMemorySize() {
        if (isSDCardExist()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    public static long getAvailableMemorySize(String path) {
        if (!TextUtils.isEmpty(path)) {
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        return -1;
    }

    public static long getTotalMemorySize(String path) {
        if (!TextUtils.isEmpty(path)) {
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
        return -1;
    }

    // 判断SD卡下external_sd文件夹的总大小
    public static double getTotalExternal_SDMemorySize() {
        if (isSDCardExist()) {
            File path = Environment.getExternalStorageDirectory();
            File externalSD = new File(path.getPath() + "/external_sd");
            if (externalSD.exists() && externalSD.isDirectory()) {
                StatFs stat = new StatFs(path.getPath() + "/external_sd");
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                if (getTotalExternalMemorySize() != -1 && getTotalExternalMemorySize() != totalBlocks * blockSize) {
                    return totalBlocks * blockSize;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }

        } else {
            return -1;
        }
    }


    private static int wifiReceivedApkVer;

    public static int getWifiReceivedApkVer() {
        return wifiReceivedApkVer;
    }

    /**
     * 获取额外的SD扩展卡,只对HONEYCOMB版本以上有效
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getExtraSDCards(Context context) {

        ArrayList<String> sdCards = new ArrayList<>();

        String sdRoot = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                sdRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            try {
                String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths").invoke(sm);
                if (paths == null) {
                    return sdCards;
                }
                if (sdRoot == null) {
                    sdRoot = "";
                }
                for (String path : paths) {
                    if (path != null) {

                        if (!sdRoot.equals(path)) {
                            if (new File(path).canWrite() && checkSDCardMount(context, path)) {
                                sdCards.add(path);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sdCards;
    }

    // 判断sdcard是否挂载上，返回值为true证明挂载上了，否则不存在
    public static boolean checkSDCardMount(Context context, String mountPoint) {
        if (mountPoint == null) {
            return false;
        }
        String state = null;
        state = getVolumeState(context, mountPoint);
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String getVolumeState(Context context, String mountPoint) {
        // mountPoint是挂载点名Storage'paths[1]:/mnt/extSdCard不是/mnt/extSdCard/
        // 不同手机外接存储卡名字不一样。/mnt/sdcard
        String status = "";
        StorageManager sm = null;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO){
                sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            }
            if (sm != null){
                Method mMethodGetPathsState = null;
                try {
                    mMethodGetPathsState = sm.getClass().getMethod("getVolumeState", String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }// String.class形参列表
                if (mMethodGetPathsState != null){
                    status = (String) mMethodGetPathsState.invoke(sm, mountPoint);
                    // 调用该方法，mStorageManager是主调，mountPoint是实参数
                }
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d(TAG, "VolumnState:" + status);
        return status;
    }

}