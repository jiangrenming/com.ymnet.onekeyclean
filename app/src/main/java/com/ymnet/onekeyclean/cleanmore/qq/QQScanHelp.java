package com.ymnet.onekeyclean.cleanmore.qq;

import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.Constants;
import com.ymnet.onekeyclean.cleanmore.constants.QQConstants;
import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQContent;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileDefault;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQPicMode;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQReceiveMode;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.FileTreeUtils;
import com.ymnet.onekeyclean.cleanmore.utils.QQUtil;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.DataUpdateListener;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;

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
public class QQScanHelp {

    private static QQScanHelp instance;
    private        QQContent  content;
    private        long       exitTime;
    private        boolean    scanFinish;

    private QQScanHelp() {
        scanFinish = false;
    }

    public boolean isScanFinish() {
        return scanFinish;
    }

    public QQFileType get(int position) {
        return content != null ? content.get(position) : null;
    }

    public void sizeDecreasing(long value) {
        if (content != null) {
            content.sizeDecreasing(value);
        }
    }

    public static QQScanHelp getInstance() {
        if (instance == null) {
            synchronized (QQScanHelp.class) {
                if (instance == null) {
                    instance = new QQScanHelp();
                }
            }
        }
        return instance;
    }

    private volatile boolean threadStop = false;

    public QQContent getDatas() {
        return content;
    }

    private String getString(int id) {
        return C.get().getString(id);
    }

    //QQ获取各种数据处
    public QQContent getInitData() {
        boolean isResetData = (System.currentTimeMillis() - exitTime) > 5 * TimeConstants.MINUTE;
        //大于五分钟才显示再次扫描
        if (content == null || isResetData) {
            exitTime = System.currentTimeMillis();
            scanFinish = false;
            if (content == null) {
                content = new QQContent();
            } else {
                content.clear();
            }
            // TODO: 2017/5/3 0003 QQ扫描路径要修改
            QQFileType tempFile = new QQFileDefault(getString(R.string.temp_file), 0, R.drawable.wechat_temp, getString(R.string.temp_file_info), getString(R.string.temp_file_effect));
            tempFile.setType(QQConstants.QQ_TYPE_DEFALUT);
            content.add(tempFile);

            QQFileType picFile = new QQPicMode(getString(R.string.chat_pic), 0, R.drawable.wechat_pic, getString(R.string.qq_chat_pic_info), "");
            picFile.setType(QQConstants.QQ_TYPE_PIC);
            content.add(picFile);

            QQFileType voiceFile = new QQPicMode(getString(R.string.chat_voice), 0, R.drawable.wechat_voice, getString(R.string.chat_voice_info), "");
            voiceFile.setType(QQConstants.QQ_TYPE_VOICE);
            content.add(voiceFile);

            QQFileType video = new QQPicMode(getString(R.string.chat_video), 0, R.drawable.wechat_video, getString(R.string.chat_video_info), "");
            video.setType(QQConstants.QQ_TYPE_PIC);
            content.add(video);
            //todo 微信QQ不同之处:接收的文件
            QQFileType receiveFile = new QQReceiveMode(getString(R.string.receive_file), 0, R.drawable.receive_icon, getString(R.string.receive_file_info), getString(R.string.receive_file_effect));

            receiveFile.setType(QQConstants.QQ_TYPE_RECEIVE);
            content.add(receiveFile);

            startScanFile(content);
        }
        return content;
    }


    /**
     * 把一个list集合中的数据 转换到other中的map集合中
     *
     * @param temp  待转换的集合
     * @param other 类型为QQFileOther
     */
    private void convertToOtherType(List<WareFileInfo> temp, QQFileType other) {
        convert(temp, other);
    }

    private void convertToOtherType2(List<WareFileInfo> temp, QQFileType other) {
        convert2(temp, other);
    }
int count;
    // TODO: 2017/5/8 0008 接收文件日期添加该方法
    private void convert(List<WareFileInfo> temp, QQFileType pics) {
        if (temp == null || temp.size() == 0)
            return;
        if (pics != null && pics instanceof QQPicMode) {
            sort(temp);
            for (WareFileInfo info : temp) {
                if (checkThread()) {
                    return;
                }

                long time = System.currentTimeMillis() - info.time;
                if (time > 6 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((QQPicMode) pics).get(QQConstants.QQ_TIME_STATUE_SIX);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_SIX);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else if (time > 3 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((QQPicMode) pics).get(QQConstants.QQ_TIME_STATUE_THREE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_THREE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else if (time > 1 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((QQPicMode) pics).get(QQConstants.QQ_TIME_STATUE_ONE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_ONE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else {
                    ListDataMode mode = ((QQPicMode) pics).get(QQConstants.QQ_TIME_STATUE_ONE_BEFORE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_ONE_BEFORE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQPicMode) pics).add(mode);
                    } else {
                        mode.add(info);
                    }

                }

            }
        }
    }

    // TODO: 2017/5/8 0008 修改类型
    private void convert2(List<WareFileInfo> temp, QQFileType receive) {
        if (temp == null || temp.size() == 0)
            return;
        if (receive != null && receive instanceof QQReceiveMode) {
            sort(temp);
            for (WareFileInfo info : temp) {
                if (checkThread()) {
                    return;
                }

                long time = System.currentTimeMillis() - info.time;
                if (time > 6 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((QQReceiveMode) receive).get(QQConstants.QQ_TIME_STATUE_SIX);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_SIX);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQReceiveMode) receive).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else if (time > 3 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((QQReceiveMode) receive).get(QQConstants.QQ_TIME_STATUE_THREE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_THREE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQReceiveMode) receive).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else if (time > 1 * 30 * TimeConstants.DAY) {
                    ListDataMode mode = ((QQReceiveMode) receive).get(QQConstants.QQ_TIME_STATUE_ONE);
                    if (mode == null) {
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_ONE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQReceiveMode) receive).add(mode);
                    } else {
                        mode.add(info);
                    }
                } else {
                    count++;
                    ListDataMode mode = ((QQReceiveMode) receive).get(QQConstants.QQ_TIME_STATUE_ONE_BEFORE);
                    if (mode == null) {
                        Log.d(TAG, "convert1: "+count);
                        mode = new ListDataMode();
                        mode.setName(QQConstants.QQ_TIME_STATUE_ONE_BEFORE);
                        mode.setExpand(false);
                        mode.add(info);
                        ((QQReceiveMode) receive).add(mode);
                    } else {
                        Log.d(TAG, "convert2: "+count);
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


    public long getQQTrustSize() {
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


    private void startScanFile(final QQContent datas) {
        if (checkStorageState()) {
            Task.BACKGROUND_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    sleepLittle(System.currentTimeMillis());

                    String[] tempAccountPaths = QQUtil.createTempAccountPaths();
                    // /tencent/MicroMsg/***/music
                    String[] cacheAccountPaths = QQUtil.createCacheAccountPaths();
                    // /tencent/MicroMsg/***/image2
                    String[] picAccountPaths = QQUtil.createPicAccountPaths();
                    // /tencent/MicroMsg/***/voice2
                    String[] voiceAccountPaths = QQUtil.createVoiceAccountPaths();
                    // /tencent/MicroMsg/***/video
                    String[] videoAccountPaths = QQUtil.createVideoAccountPaths();
                    Long start = 0L;
                    long size = datas.length();
                    Log.d(TAG, "run: size:" + size);
                    for (int i = 0; i < size; i++) {
                        if (checkThread()) {
                            break;
                        }
                        QQFileType type = datas.get(i);
                        if (getString(R.string.temp_file).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanTempFile(type, tempAccountPaths);
                            sleepLittle(start);
                            Log.i("Tagd-", type.toString());
                        } else if (getString(R.string.receive_file).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            Log.i("Tagd--", type.toString());
                            scanCacheFile(type, cacheAccountPaths);
                            sleepLittle(start);
                        } else if (getString(R.string.chat_pic).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanPicFile(type, picAccountPaths);
                            sleepLittle(start);
                            Log.i("Tagd---", type.toString());
                        } else if (getString(R.string.chat_voice).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanVoiceFile(type, voiceAccountPaths);
                            sleepLittle(start);
                            Log.i("Tagd----", type.toString());
                        } else if (getString(R.string.chat_video).equals(type.getFileName())) {
                            start = System.currentTimeMillis();
                            scanVideoFile(type, videoAccountPaths);
                            sleepLittle(start);
                            Log.i("Tagd------", type.toString());
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
        }
    }


    /**
     * 扫描QQ视频垃圾
     *
     * @param QQFile 初始化好的数据结构  类型应该是other
     * @param paths  根目录
     */
    private void scanVideoFile(QQFileType QQFile, String[] paths) {
        if (QQFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> temp = new ArrayList<>();
           /* if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }

                    getData(QQFile, path, temp, QQConstants.VIDEO_SUFFIX_ACCOUNT);
                }
            }*/
            if (QQConstants.VIDEO_PATH != null) {
                for (String path : QQConstants.VIDEO_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(QQFile, path, temp, QQConstants.VIDEO_SUFFIX);
                }
            }
            convertToOtherType(temp, QQFile);
        }
    }


    /**
     * 扫描语音垃圾
     *
     * @param QQFile 初始化好的数据结构  类型应该是other
     * @param paths  根目录
     */
    private void scanVoiceFile(QQFileType QQFile, String[] paths) {
        if (QQFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> temp = new ArrayList<>();
            if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }
                    getData(QQFile, path, temp, QQConstants.VOICE_SUFFIX);
                }
            }
            if (QQConstants.VOICE_PATH != null) {
                for (String path : QQConstants.VOICE_PATH) {
                    if (checkThread()) {
                        return;
                    }
                    getData(QQFile, path, temp, null);
                }
            }
            convertToOtherType(temp, QQFile);

        }
    }

    /**
     * 扫描QQ照片垃圾
     *
     * @param QQFile 初始化好的数据结构  类型应该是other
     * @param paths  根目录
     */
    private void scanPicFile(QQFileType QQFile, String[] paths) {
        if (QQFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> temp = new ArrayList<>();
            if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }
                    getData(QQFile, path, temp, null);
                }
            }
            if (QQConstants.PIC_PATH != null) {
                for (String path : QQConstants.PIC_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(QQFile, path, temp, QQConstants.PIC_SUFFIX);

                }
            }
            convertToOtherType(temp, QQFile);

        }
    }

    /**
     * 扫描QQ的接收的文件
     *
     * @param QQFile 初始化好的数据结构 类型应该是default
     * @param paths  跟目录
     */
    private void scanCacheFile(QQFileType QQFile, String[] paths) {
        Log.d(TAG, "scanCacheFile: " + "扫描QQ接收文件1");
        if (QQFile != null && !TextUtils.isEmpty(sdPath)) {
            Log.d(TAG, "scanCacheFile: " + "扫描QQ接收文件2");
            List<WareFileInfo> list = ((QQReceiveMode) QQFile).getFilePaths();
            if (paths != null) {
                for (String path : paths) {
                    if (checkThread()) {
                        return;
                    }
                    getReceiveFileData(QQFile, path, list, QQConstants.RECEIVE_FILE_SUFFIX);

                }
            }
           /* if (QQConstants.RECEIVE_FILE_ACCOUNT != null) {
                Log.d(TAG, "scanCacheFile: " + "扫描QQ接收文件3");
                for (String path : QQConstants.RECEIVE_FILE_ACCOUNT) {
                    if (checkThread()) {
                        return;
                    }
                    getReceiveFileData(QQFile, path, list, QQConstants.RECEIVE_FILE_SUFFIX);
                }

            }*/
            convertToOtherType2(list, QQFile);
        }

    }


    /**
     * 扫描QQ的临时文件
     *
     * @param QQFile 初始化好的数据结构 weChatFile 的类型为Default
     * @param paths  根目录
     */
    private void scanTempFile(QQFileType QQFile, String[] paths) {
        if (QQFile != null && !TextUtils.isEmpty(sdPath)) {
            List<WareFileInfo> list = ((QQFileDefault) QQFile).getFilePaths();
            Log.d(TAG, "scanTempFile: list数据 " + list.toString());
            //1.扫描账户目录
            if (paths != null) {
                int length = paths.length;
                for (int i = 0; i < length; i++) {
                    if (checkThread()) {
                        return;
                    }
                    String path = paths[i];
                    // TODO: 2017/4/25 0025
                    //                    getData(QQFile, path, list, null);
                    getData(QQFile, path, list, null);
                }
            }
            //2.扫描公共目录
            if (QQConstants.TEMP_PATH != null) {
                for (String path : QQConstants.TEMP_PATH) {
                    if (checkThread()) {
                        return;
                    }

                    getData(QQFile, path, list, null);
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
    // TODO: 2017/5/4 0004 添加文件格式
    private List<WareFileInfo> getData(QQFileType source, File file, List<WareFileInfo> res, String[] suffixs) {
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

    private List<WareFileInfo> getReceiveFileData(QQFileType source, String path, List<WareFileInfo> res, String[] suffixs) {
        return getReceiveFileData(source, new File(sdPath, path), res, suffixs);
    }

    private List<WareFileInfo> getReceiveFileData(QQFileType source, File file, List<WareFileInfo> res, String[] suffixs) {
        if (threadStop) {
            return res;
        }
        if (file == null || !file.exists()) {
            return res;
        }
        if (res == null) {
            res = new ArrayList<>();
        }

        if (!file.isFile()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        long tempSize = f.length();
                        if (tempSize > 0 && checkName(file.getName(), suffixs)) {
                            res.add(new WareFileInfo(file.getAbsolutePath(), file.lastModified(),f.getName(), tempSize));
                            source.setScanOldSize(source.getScanOldSize() + tempSize);
                            source.setCurrentSize(source.getScanOldSize());
                            content.sizeImmersive(tempSize);
                            if (listener != null) {
                                listener.update();
                            }
                        }
                    }

                }
            }

        }

        return res;
    }

    private List<WareFileInfo> getData(QQFileType source, String path, List<WareFileInfo> res, String[] suffixs) {
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

    public boolean isInstalled() {
        List<ApplicationInfo> installedApplications =  C.get().getPackageManager().getInstalledApplications(0);
        for (int i = 0; i < installedApplications.size(); i++) {
            System.out.println("已安装应用进程名:"+installedApplications.get(i).processName);
            if (installedApplications.get(i).processName.equals("com.tencent.mobileqq")) {
                return true;
            }
        }
        return false;
    }
}
