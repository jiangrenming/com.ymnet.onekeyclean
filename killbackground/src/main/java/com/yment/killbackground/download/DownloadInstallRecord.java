package com.yment.killbackground.download;

import android.content.Context;
import android.net.Uri;

import com.example.commonlibrary.utils.ShareDataUtils;
import com.yment.killbackground.utils.FileUtilsSdk;
import com.yment.killbackground.utils.PackageUtils;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/19.
 */
@Deprecated
public class DownloadInstallRecord {
    private static String DOWNLOAD_RECORD = "download";
    private static String INSTALL_RECORD = "install";

    public static void setDownloadRecodr(Context context, String packageName, String uri){
        ShareDataUtils.setSharePrefData(context,DOWNLOAD_RECORD,packageName,uri);
    }

    public static void setInstallRecord(Context context, String packageName, String path){
        ShareDataUtils.setSharePrefData(context,INSTALL_RECORD,packageName,path);
    }

    public static void removeDownloadRecodr(Context context, String packageName){
        ShareDataUtils.removeSharePrefData(context,DOWNLOAD_RECORD,packageName);
    }

    public static void removeInstallRecord(Context context, String packageName){
        ShareDataUtils.removeSharePrefData(context,INSTALL_RECORD,packageName);
    }

    public static void getDownloadRecordAll(Context context){
        Map<String,?> map= ShareDataUtils.getSharePrefDataAll(context,DOWNLOAD_RECORD);
        if(map == null || map.size() == 0)return;
        Iterator<String> keys = map.keySet().iterator();
        while(keys.hasNext()) {
            String pkg = keys.next();
            String uri = (String) map.get(pkg);
            if(PackageUtils.isAppInstalled(context,pkg)){
                try {
                    File file = new File(FileUtilsSdk.getFilesDirPath(context) + pkg + FileUtilsSdk.APK_FILE_SUFFIX);
                    File fileTemp = new File(FileUtilsSdk.getFilesDirPath(context) + pkg + FileUtilsSdk.APK_FILE_SUFFIX
                            + FileUtilsSdk.COMMONSDK_TEMP_FILE_SUFFIX);
                    if (file.exists()) {
                        if(fileTemp.exists()){
                            FileUtilsSdk.deleteFile(fileTemp);
                        }
                        FileUtilsSdk.deleteFile(file);
                        removeDownloadRecodr(context,pkg);
                    }else {
                        removeDownloadRecodr(context,pkg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                PushManager.getInstance().downloadApkFile(context, pkg, "JPushTestSign",
                        1, false, Uri.parse(uri.trim().toString()), true);
            }

        }
    }

    public static void getInstallRecordAll(Context context){
        Map<String,?> map= ShareDataUtils.getSharePrefDataAll(context,INSTALL_RECORD);
        Iterator<String> keys = map.keySet().iterator();
        while(keys.hasNext()) {
            String pkg = keys.next();
            String path = (String) map.get(pkg);
            Uri uri = Uri.parse(path);
            if(PackageUtils.isAppInstalled(context,pkg)){
                try {
                    File file = new File(new URI(uri.toString()));
                    FileUtilsSdk.deleteFile(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                PushManager.getInstance().installApk(context,pkg,uri,"JPushTestSign",1,false);
            }

        }
    }
}
