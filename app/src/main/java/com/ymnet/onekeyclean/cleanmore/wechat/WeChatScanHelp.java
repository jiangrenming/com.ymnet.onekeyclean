package com.ymnet.onekeyclean.cleanmore.wechat;

import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.Constants;
import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.constants.WeChatConstants;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.FileTreeUtils;
import com.ymnet.onekeyclean.cleanmore.utils.WeChatUtil;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.DataUpdateListener;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatContent;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileDefault;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileType;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatPicMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bolts.Task;

import static android.content.ContentValues.TAG;


/**
 * Created by wangduheng26 on 4/1/16.
 */
public class WeChatScanHelp {

    private static WeChatScanHelp instance;
    private        WeChatContent  content;
    private        long           exitTime;
    private        boolean        scanFinish;
    private        boolean        mInstalled;

    private WeChatScanHelp() {
        scanFinish = false;
    }

    public boolean isScanFinish() {
        return scanFinish;
    }

    public WeChatFileType get(int position) {
        return content != null ? content.get(position) : null;
    }

    public void sizeDecreasing(long value) {
        if (content != null) {
            content.sizeDecreasing(value);
        }
    }

    public static WeChatScanHelp getInstance() {
        if (instance == null) {
            synchronized (WeChatScanHelp.class) {
                if (instance == null) {
                    instance = new WeChatScanHelp();
                }
            }
        }
        return instance;
    }

    private volatile boolean threadStop = false;

    public WeChatContent getDatas() {
        return content;
    }

    private String getString(int id) {
        return C.get().getString(id);
    }

    //微信获取各种数据处
    public WeChatContent getInitData() {
        boolean isResetData = (System.currentTimeMillis() - exitTime) > 5 * TimeConstants.MINUTE;
        //大于五分钟才显示再次扫描
        if (content == null || isResetData) {
            exitTime = System.currentTimeMillis();
            scanFinish = false;
            if (content == null) {
                content = new WeChatContent();
            } else {
                content.clear();
            }
            WeChatFileType tempFile = new WeChatFileDefault(getString(R.string.temp_file), 0, R.drawable.wechat_temp, getString(R.string.temp_file_info), getString(R.string.temp_file_effect));
            tempFile.setType(WeChatConstants.WECHAT_TYPE_DEFALUT);
            //            tempFile.setsE(StatisticEventContants.cleanwechat_rubbish);
            content.add(tempFile);

            WeChatFileType cacheFile = new WeChatFileDefault(getString(R.string.cache_file), 0, R.drawable.wechat_cache, getString(R.string.cache_file_info), getString(R.string.cache_file_effect));
            cacheFile.setType(WeChatConstants.WECHAT_TYPE_DEFALUT);
            //            cacheFile.setsE(StatisticEventContants.cleanwechat_snscache);
            content.add(cacheFile);

            WeChatFileType picFile = new WeChatPicMode(getString(R.string.chat_pic), 0, R.drawable.wechat_pic, getString(R.string.chat_pic_info), "");
            picFile.setType(WeChatConstants.WECAHT_TYPE_PIC);
            //            picFile.setsE(StatisticEventContants.cleanwechat_photo);
            content.add(picFile);

            WeChatFileType voiceFile = new WeChatPicMode(getString(R.string.chat_voice), 0, R.drawable.wechat_voice, getString(R.string.chat_voice_info), "");
            voiceFile.setType(WeChatConstants.WECHAT_TYPE_VOICE);
            //            voiceFile.setsE(StatisticEventContants.cleanwechat_voice);
            content.add(voiceFile);

            WeChatFileType video = new WeChatPicMode(getString(R.string.chat_video), 0, R.drawable.wechat_video, getString(R.string.chat_video_info), "");
            video.setType(WeChatConstants.WECAHT_TYPE_PIC);
            //            video.setsE(StatisticEventContants.cleanwechat_video);
            content.add(video);

            startScanFile(content);
        }
        return content;
    }


    /**
     * 把一个list集合中的数据 转换到other中的map集合中
     *
     * @param temp  待转换的集合
     * @param other 类型为WeChatFileOther
     */
    private void convertToOtherType(List<WareFileInfo> temp, WeChatFileType other) {
        convert(temp, other);
    }

    private void convert(List<WareFileInfo> temp, WeChatFileType pics) {
        if (temp == null || temp.size() == 0)
            return;
        if (pics != null && pics instanceof WeChatPicMode) {
            sort(temp);
            for (WareFileInfo info : temp) {
                if (checkThread()) {
                    return;
                }

                long time = System.currentTimeMillis() - info.time;
                if (time > 6 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((WeChatPicMode) pics).get(WeChatConstants.WECHAT_TIME_STATUE_SIX);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(WeChatConstants.WECHAT_TIME_STATUE_SIX);
                        mode.setExpand(false);
                        mode.add(info);
                        ((WeChatPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else if (time > 3 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((WeChatPicMode) pics).get(WeChatConstants.WECHAT_TIME_STATUE_THREE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(WeChatConstants.WECHAT_TIME_STATUE_THREE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((WeChatPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else if (time > 1 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((WeChatPicMode) pics).get(WeChatConstants.WECHAT_TIME_STATUE_ONE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(WeChatConstants.WECHAT_TIME_STATUE_ONE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((WeChatPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else {
                    ListDataMode mode = ((WeChatPicMode) pics).get(WeChatConstants.WECHAT_TIME_STATUE_ONE_BEFORE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(WeChatConstants.WECHAT_TIME_STATUE_ONE_BEFORE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((WeChatPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }
                }

            }
        }
    }

    private void sort(List<WareFileInfo> temp) {
        if (temp == null || temp.size() == 0)
            return;
        Collections.sort(temp, new Comparator<WareFileInfo>() {
            @Override
            public int compare(WareFileInfo lhs, WareFileInfo rhs) {
                if (lhs.time > rhs.time)
                    return -1;
                else if (lhs.time < rhs.time)
                    return 1;
                return 0;
            }
        });
    }


    public long getWeChatTrustSize() {
        return content == null ? 0 : content.getSize();
    }

    private DataUpdateListener listener;

    public DataUpdateListener getListener() {
        return listener;
    }

    public void setUpdateListener(DataUpdateListener listener) {
        this.listener = listener;
    }

    public long getExitTime() {
        return exitTime;
    }

    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public int getCount() {
        return content == null ? 0 : content.length();
    }


    private void startScanFile(final WeChatContent datas) {
        //先扫描是否安装微信app
        if (checkStorageState()) {
            Task.BACKGROUND_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    sleepLittle(System.currentTimeMillis());
                    /**
                     * /tencent/MicroMsg/cache
                     * /tencent/MicroMsg/wvtemp
                     * /tencent/MicroMsg/WebviewCache
                     * /tencent/MicroMsg/vusericon
                     */
                    String[] tempAccountPaths = WeChatUtil.createTempAccountPaths();
                    // /tencent/MicroMsg/***/music
                    String[] cacheAccountPaths = WeChatUtil.createCacheAccountPaths();
                    // /tencent/MicroMsg/***/image2
                    String[] picAccountPaths = WeChatUtil.createPicAccountPaths();
                    // /tencent/MicroMsg/***/voice2
                    String[] voiceAccountPaths = WeChatUtil.createVoiceAccountPaths();
                    // /tencent/MicroMsg/***/video
                    String[] videoAccountPaths = WeChatUtil.createVideoAccountPaths();
                    Long start = 0L;
                    long size = datas.length();
                    for (int i = 0; i < size; i++) {
                        if (checkThread()) {
                            break;
                        }
                        WeChatFileType type = datas.get(i);
                        if (getString(R.string.temp_file).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanTempFile(type, tempAccountPaths);
                            sleepLittle(start);
                        } else if (getString(R.string.cache_file).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanCacheFile(type, cacheAccountPaths);
                            sleepLittle(start);
                        } else if (getString(R.string.chat_pic).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanPicFile(type, picAccountPaths);
                            sleepLittle(start);
                        } else if (getString(R.string.chat_voice).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanVoiceFile(type, voiceAccountPaths);
                            sleepLittle(start);
                        } else if (getString(R.string.chat_video).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanVideoFile(type, videoAccountPaths);
                            sleepLittle(start);
                        }
                        type.setInEndAnim(true);
                        if (listener != null)
                            listener.update();
                    }
                    scanFinish = true;
                    if (listener != null)
                        listener.updateEnd();
                }


            });
        } else {
            scanFinish = true;
            if (listener != null)
                listener.updateEnd();
        }

    }

    private void sleepLittle(Long start) {
        try {
            if (System.currentTimeMillis() - start < 1000) {
                Thread.sleep(1000);
                start = System.currentTimeMillis();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(),e.fillInStackTrace());
        }
    }


    /**
     * 扫描微信视频垃圾
     *
     * @param weChatFile 初始化好的数据结构  类型应该是other
     * @param paths      根目录
     */
    private void scanVideoFile(WeChatFileType weChatFile, String[] paths) {
        if (weChatFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> temp = new ArrayList<>();
            /*if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, temp, WeChatConstants.VIDEO_SUFFIX_ACCOUNT);
                }
            }*/
            if (WeChatConstants.VIDEO_PATH != null) {
                for (String path : WeChatConstants.VIDEO_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, temp, WeChatConstants.VIDEO_SUFFIX);
                }
            }
            convertToOtherType(temp, weChatFile);
        }
    }


    /**
     * 扫描语音垃圾
     *
     * @param weChatFile 初始化好的数据结构  类型应该是other
     * @param paths      根目录
     */
    private void scanVoiceFile(WeChatFileType weChatFile, String[] paths) {
        if (weChatFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> temp = new ArrayList<>();
            if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, temp, WeChatConstants.VOICE_SUFFIX);
                }
            }
            if (WeChatConstants.VOICE_PATH != null) {
                for (String path : WeChatConstants.VOICE_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, temp, null);
                }
            }
            convertToOtherType(temp, weChatFile);

        }
    }

    /**
     * 扫描微信照片垃圾
     *
     * @param weChatFile 初始化好的数据结构  类型应该是other
     * @param paths      根目录
     */
    private void scanPicFile(WeChatFileType weChatFile, String[] paths) {
        if (weChatFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> temp = new ArrayList<>();
            /*if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }
                    getData(weChatFile, path, temp, null);
                }
            }*/
            if (WeChatConstants.PIC_PATH != null) {
                for (String path : WeChatConstants.PIC_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, temp, WeChatConstants.PIC_SUFFIX);

                }
            }
            convertToOtherType(temp, weChatFile);

        }
    }

    /**
     * 扫描微信的缓存垃圾
     *
     * @param weChatFile 初始化好的数据结构 类型应该是default
     * @param paths      跟目录
     */
    private void scanCacheFile(WeChatFileType weChatFile, String[] paths) {
        if (weChatFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> list = ((WeChatFileDefault) weChatFile).getFilePaths();
            if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, list, null);
                }
            }
            if (WeChatConstants.CACHE_PATH != null) {
                for (String path : WeChatConstants.CACHE_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, list, null);
                }
            }

        }

    }

    /**
     * 扫描微信的临时文件
     *
     * @param weChatFile 初始化好的数据结构 weChatFile 的类型为Default
     * @param paths      根目录
     */
    private void scanTempFile(WeChatFileType weChatFile, String[] paths) {
        if (weChatFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> list = ((WeChatFileDefault) weChatFile).getFilePaths();
            Log.d(TAG, "scanTempFile: list数据 " + list.toString());
            //1.扫描账户目录
            if (paths != null) {
                int length = paths.length;
                for (int i = 0; i < length; i++) {
                    if (checkThread()) {
                        return;
                    }
                    String path = paths[i];
                    getData(weChatFile, path, list, null);
                }
            }
            //2.扫描公共目录
            if (WeChatConstants.TEMP_PATH != null) {
                for (String path : WeChatConstants.TEMP_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(weChatFile, path, list, null);
                }
            }

        }

    }

    private String sdPath;

    private boolean checkStorageState() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            sdPath = Constants.SDCard;
            return true;
        }
        return false;
    }

    /**
     * 递归调用 file目录下 suffixs后缀的文件 返回在res集合中
     *
     * @param file    跟目录
     * @param res     返回的数据集合
     * @param suffixs 需要的文件后缀 数组
     * @return
     */
    private List<WareFileInfo> getData(WeChatFileType source, File file, List<WareFileInfo> res, String[] suffixs) {
        if (threadStop) {
            return res;
        }
        if (file == null || !file.exists()) {
            return res;
        }
        if (res == null) {
            res = new ArrayList<>();
        }

        if (file.isFile()) {
            long tempSize = file.length();
            if (tempSize > 0 && checkName(file.getName(), suffixs)) {
                res.add(new WareFileInfo(file.getAbsolutePath(), file.lastModified(), tempSize));
                source.setScanOldSize(source.getScanOldSize() + tempSize);
                source.setCurrentSize(source.getScanOldSize());
                content.sizeImmersive(tempSize);
                if (listener != null)
                    listener.update();
            }

        } else {
            //文件数量大于15才清理
            if (!FileTreeUtils.isFileDirOver(file, 15)) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        getData(source, f, res, suffixs);
                    }
                }

            }
        }
        return res;
    }

    private List<WareFileInfo> getData(WeChatFileType source, String path, List<WareFileInfo> res, String[] suffixs) {

        return getData(source, new File(sdPath, path), res, suffixs);
    }


    private boolean checkName(String name, String[] suffixs) {
        if (TextUtils.isEmpty(name))
            return false;
        if (suffixs == null || suffixs.length == 0)
            return true;
        for (String suffix : suffixs) {
            if (name.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }


    private boolean checkThread() {
        return threadStop;
    }

    //是否安装该应用
    public boolean isInstalled() {
        List<ApplicationInfo> installedApplications =  C.get().getPackageManager().getInstalledApplications(0);
        for (int i = 0; i < installedApplications.size(); i++) {
            System.out.println("已安装应用进程名:"+installedApplications.get(i).processName);
            if (installedApplications.get(i).processName.equals("com.tencent.mm")) {
                return true;
            }
        }
        return false;
    }

    public void setInstalled(boolean installed) {
        mInstalled = installed;
    }
}
