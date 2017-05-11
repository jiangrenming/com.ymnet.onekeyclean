package com.ymnet.onekeyclean.cleanmore.cacheclean;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.common.StreamUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Util
{
    
    /**
     * <获取文件夹或者文件大小>
     *
     *            path 路径或者文件
     * @throw
     * @return String 文件的大小，以BKMG来计量
     */
    public static String getPathSize(String path)
    {
        String flieSizesString = "";
        File file = new File(path.trim());
        long fileSizes = 0;
        if (null != file && file.exists())
        {
            if (file.isDirectory())
            { // 如果路径是文件夹的时候
                fileSizes = getFileFolderTotalSize(file);
            }
            else if (file.isFile())
            {
                fileSizes = file.length();
            }
        }
        flieSizesString = ApplicationUtils.formatFileSizeToString(fileSizes);
        return flieSizesString;
    }

    /**
     * 删除指定文件夹下除指定文件之外的所有文件
     *
     * @param  fileDir 被删除的文件夹目录
     *
     * @return void
     */

    public static long getFileFolderTotalSize(File fileDir)
    {
        long totalSize = 0;
        if (null != fileDir && fileDir.exists())
        {
            File fileList[] = fileDir.listFiles();
            if (fileList != null && fileList.length > 0)
            {
                for (int fileIndex = 0; fileIndex < fileList.length; fileIndex++)
                {
                    if (fileList[fileIndex].isDirectory())
                    {
                        totalSize = totalSize + getFileFolderTotalSize(fileList[fileIndex]);
                    }
                    else
                    {
                        totalSize = totalSize + fileList[fileIndex].length();
                    }
                }
            }
        }
        return totalSize;
    }

    /**
     * Android 安装应用
     *
     * @param context
     *            Application Context
     * @param file APK文件路径
     *
     */
    public static void installApk(Context context, File file)
    {
        if (file.exists())
        {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            ((ContextWrapper) context).startActivity(i);
        }
        else
        {
            Toast.makeText(context, context.getResources().getString(R.string.install_fail_file_not_exist), Toast.LENGTH_SHORT).show();
        }
    }

    /** 若将整个文件夹删除，则只需调用这个方法 */
    public static void clear(File file)
    {
        if (file.exists())
        { // 指定文件是否存在
            if (file.isFile())
            { // 该路径名表示的文件是否是一个标准文件
                file.delete(); // 删除该文件
            }
            else if (file.isDirectory())
            { // 该路径名表示的文件是否是一个目录（文件夹）
                File[] files = file.listFiles(); // 列出当前文件夹下的所有文件
                if (files != null && files.length > 0)
                {
                    for (File f : files)
                    {
                        clear(f); // 递归删除
                    }
                }
            }
            file.delete(); // 删除文件夹（song,art,lyric）
        }
    }

    /**
     * * 复制assets中的文件到指定目录下 * @param context * @param assetsFileName * @param
     * targetPath * @return
     */
    public static boolean copyAssetData(Context context, String assetsFileName, String targetPath)
    {
        try
        {
            InputStream inputStream = context.getAssets().open(assetsFileName);
            FileOutputStream output = new FileOutputStream(targetPath + File.separator + assetsFileName);
            byte[] buf = new byte[10240];
            int count = 0;
            while ((count = inputStream.read(buf)) > 0)
            {
                output.write(buf, 0, count);
            }
            output.close();
            inputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void setClearSize(Context context, String size)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("clear_size", size);
        editor.commit();
    }

    public static String getClearSize(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("clear_size", "");
    }

    public static void setClearDate(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("clear_date", System.currentTimeMillis());
        editor.commit();
    }

    /**
     * update from wdh
     * @param context
     * @param currentTime
     * @return 返回最后的修改时间 跟现在毫秒数的差值：毫秒数，  没有使用过的话则返回currentTime
     */
    public static long getLaseCleanDate(Context context, long currentTime)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long laseTime = sp.getLong("clear_date", 0);
        long dur = currentTime - laseTime ;

        return dur;
    }
    public static String getOutSDPath(Context context)
    {
        String outPath = null;
        ArrayList<String> list = (ArrayList<String>) getExtSDCardPaths();
        if (list != null && list.size() > 0)
        {
            outPath = list.get(0);
        }
        String external = getSDPath(context);
        if (external != null && outPath != null && !external.equals(outPath))
        {
            File file = new File(outPath);
            if (file.exists())
            {
                return outPath;
            }
        }
        return outPath;
    }

    /*
     * 获得SD卡目录，默認內置的
     */
    public static String getSDPath(Context context)
    {
        String sdDir = null;
        boolean sdCardExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()); // 判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory().toString();// 获取跟目录
        }
        else
        {
            sdDir = null;
        }
        return sdDir;

    }


    public static List<String> getExtSDCardPaths()
    {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.endsWith(Environment.MEDIA_UNMOUNTED) && extFile.exists() && extFile.isDirectory() && extFile.canWrite())
        {
            paths.add(extFile.getAbsolutePath());
        }

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try
        {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null)
            {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line.contains("storage")) || line.contains("secure") || line.contains("asec") || line.contains("firmware")
                        || line.contains("shell") || line.contains("obb") || line.contains("legacy") || line.contains("data"))
                {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length)
                {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data") || mountPath.contains("Data"))
                {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory() || !mountRoot.canWrite())
                {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsolutePath());
                if (equalsToPrimarySD)
                {
                    continue;
                }
                paths.add(mountPath);
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            StreamUtil.closeInputStream(is);
            StreamUtil.closeReader(isr,br);

        }
        return paths;
    }
}
