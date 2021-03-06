package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.text.TextUtils;

import com.ymnet.onekeyclean.cleanmore.constants.FileScanState;
import com.ymnet.onekeyclean.cleanmore.datacenter.MarketObservable;
import com.ymnet.onekeyclean.cleanmore.datacenter.MediaTypeForCamera;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper.FileCategory;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileSortHelper.SortMethod;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.MTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FileControl extends MarketObservable {

    private Context context;

    private boolean isRun = false;

    private boolean isRunning = false;
    private Cursor mVideoC;
    private Cursor mMusicC;
    private Cursor mPicC;
    private Cursor mMediaC;

    public boolean isUserStop() {
        return isUserStop;
    }

    public void setUserStop(boolean userStop) {
        isUserStop = userStop;
    }

    private boolean isUserStop = false;
    private FileCategoryHelper categoryHelper;

    private static volatile FileControl instance = null;

    private int allPicCount;

    private long allPicSize;

    private HashMap<String, ArrayList<FileInfo>> allPicsMap;

    private ArrayList<FileInfo> allPicsList;

    private ArrayList<String> allPicDirNames;

    private ArrayList<FileInfo> docList;

    private ArrayList<FileInfo> apkList;

    private ArrayList<FileInfo> zipList;

    private long docSize;

    private long apkSize;

    private long zipSize;

    private long scanNum;

    private HashMap<FileCategory, CategoryInfo> mCategoryInfo = new HashMap<FileCategory, CategoryInfo>();

    public static FileCategory[] sCategories = new FileCategory[]{FileCategory.Picture, FileCategory.Music, FileCategory.Video, FileCategory.Doc, FileCategory.Apk, FileCategory.Zip,
            FileCategory.Other};

    public class CategoryInfo {
        public long count;

        public long size;
    }

    private FileControl(Context context) {
        super();
        this.context = context;
        categoryHelper = new FileCategoryHelper(context);
    }

    public static FileControl getInstance(Context context) {
        if (instance == null) {
            /*synchronized (FileControl.class) {
                if (instance == null) {*/
                    instance = new FileControl(C.get());
              /*  }
            }*/
        }
        return instance;
    }

    public void scan() {
        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {

            @Override
            public void run() {
                isRun = true;
                refreshCategoryInfo();
            }
        });

    }

    /**
     * 删除数据后通知更新
     *
     * @param fc    类型
     * @param count 删除的项数
     * @param size  删除的大小
     */
    public void notifyDataChange(FileCategory fc, int count, long size) {
        if (mCategoryInfo != null) {
            CategoryInfo info = mCategoryInfo.get(fc);
            if (info == null) {
                info = new CategoryInfo();
                mCategoryInfo.put(fc, info);
            } else {
                info.count = (info.count - count);
                info.size = info.size - size;
                if (info.count < 0) info.count = 0;
                if (info.size < 0) info.size = 0;
            }
            setChanged();
            notifyObservers(FileScanState.DATA_CHANGE);
        }

    }

    public void refreshCategoryInfo() {
        isRunning = true;
        // clear
        for (FileCategory fc : sCategories) {
            setCategoryInfo(fc, 0, 0);
        }

        // query database
        String volumeName = "external";
        Uri uri = Video.Media.getContentUri(volumeName);
        refreshMediaCategory(FileCategory.Video, uri);

        uri = Audio.Media.getContentUri(volumeName);
        refreshMediaCategory(FileCategory.Music, uri);

        uri = Images.Media.getContentUri(volumeName);
        refreshAllPicCategory(FileCategory.Picture, uri);

        setChanged();
        notifyObservers(FileScanState.SCAN_PROVIDER_FINSH);

        // 遍历sd卡
        refreshMediaCategory2();
        isRunning = false;

        setChanged();
        notifyObservers(FileScanState.SCAN_FINSH);
    }

    private boolean refreshMediaCategory(FileCategory fc, Uri uri) {
        if (isRun && categoryHelper != null) {
            String[] columns = new String[]{"COUNT(*)", "SUM(_size)"};
            mMediaC = context.getContentResolver().query(uri, columns, categoryHelper.buildSelectionByCategory(fc), null, null);
            if (mMediaC == null) {
                return false;
            }

            if (mMediaC.moveToNext()) {
                setCategoryInfo(fc, mMediaC.getLong(0), mMediaC.getLong(1));
                mMediaC.close();
                return true;
            } else {
                mMediaC.close();
            }
        }
        return false;
    }

    private boolean refreshMediaCategory2() {
        if (docList == null) {
            docList = new ArrayList<FileInfo>();
        } else {
            docList.clear();
            docSize = 0;
        }

        if (apkList == null) {
            apkList = new ArrayList<FileInfo>();
        } else {
            apkList.clear();
            apkSize = 0;
        }

        if (zipList == null) {
            zipList = new ArrayList<FileInfo>();
        } else {
            zipList.clear();
            zipSize = 0;
        }
        File f = Environment.getExternalStorageDirectory();
        listSortFilesInPhoneDisk(f);
        String outPath = Util.getOutSDPath();
        if (!TextUtils.isEmpty(outPath)) {
            File file = new File(outPath);
            if (file.exists()) {
                listSortFilesInPhoneDisk(file);
            }
        }

        setCategoryInfo(FileCategory.Doc, docList.size(), docSize);
        setCategoryInfo(FileCategory.Apk, apkList.size(), apkSize);
        setCategoryInfo(FileCategory.Zip, zipList.size(), zipSize);
        return false;
    }

    /**
     * 遍历所有本机目录中的文件并统计文件信息
     *
     * @param file 遍历的文件
     * @author
     */
    private void listSortFilesInPhoneDisk(File file) {
        try {
            File[] files = file.listFiles();
            // 遍历需求
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        if (files[i].getCanonicalPath().equals(files[i].getAbsolutePath())) {
                            listSortFilesInPhoneDisk(files[i]);
                        }
                    } else {
                        // 获取文件的后缀名
                        String suffix = FileBrowserUtil.getSuffix(files[i].getName());
                        FileInfo fileInfo = getFileInfo(files[i].getPath());
                        if (fileInfo != null) {
                            if (suffix.equals("apk")) {
                                fileInfo.fc = FileCategory.Apk;
                                apkList.add(fileInfo);
                                apkSize += files[i].length();
                                scanNum += 1;
                            } else if (suffix.equals("zip") || suffix.equals("rar") || suffix.equals("iso") || suffix.equals("7z")) {
                                fileInfo.fc = FileCategory.Zip;
                                zipList.add(fileInfo);
                                zipSize += files[i].length();
                                scanNum += 1;
                            } else if ((FileBrowserUtil.isText(suffix))) {
                                fileInfo.fc = FileCategory.Doc;
                                docList.add(fileInfo);
                                docSize += files[i].length();
                                scanNum += 1;
                            }
                            if (scanNum >= 50) {
                                setCategoryInfo(FileCategory.Doc, docList.size(), docSize);
                                setCategoryInfo(FileCategory.Apk, apkList.size(), apkSize);
                                setCategoryInfo(FileCategory.Zip, zipList.size(), zipSize);
                                setChanged();
                                notifyObservers(FileScanState.SCAN_SDCARD_ING);
                                scanNum = 0;
                            }
                        }

                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private File getWXImage2Dir(File file) {
        try {
            if (file != null && file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();

                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            if (files[i].getAbsolutePath().endsWith("image2")) {
                                return files[i];
                            } else {
                                File f = getWXImage2Dir(files[i]);
                                if (f != null) {
                                    return f;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    private void getWXPic(File file) {
        try {
            if (file != null && file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File file1 : files) {
                            if (isUserStop) return;
                            if (file1.isDirectory()) {
                                getWXPic(file1);
                            } else {
                                handleWXFile(file1.getPath());
                            }
                        }
                    }
                } else {
                    handleWXFile(file.getPath());
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void handleWXFile(String path) {
        String suffix = FileBrowserUtil.getSuffix(path);
        if (FileBrowserUtil.isPic(suffix)) {
            FileInfo fileInfo = getFileInfo(path);
            if (fileInfo != null) {
                fileInfo.fc = FileCategory.Picture;
                ArrayList<FileInfo> wx = allPicsMap.get(FileBrowserUtil.PIC_DIR_WX);
                if (wx == null) {
                    wx = new ArrayList<FileInfo>();
                    allPicsMap.put(FileBrowserUtil.PIC_DIR_WX, wx);
                    int index = allPicDirNames.indexOf(FileBrowserUtil.PIC_DIR_QQ);
                    if (index == -1) {
                        index = allPicDirNames.indexOf(FileBrowserUtil.PIC_DIR_SCREENSHOTS);

                        if (index == -1) {
                            index = allPicDirNames.indexOf(FileBrowserUtil.PIC_DIR_PHOTO);
                        }
                    }

                    index++;

                    allPicDirNames.add(index, FileBrowserUtil.PIC_DIR_WX);
                }

                allPicCount++;
                allPicSize = allPicSize + fileInfo.fileSize;

                wx.add(fileInfo);
                allPicsList.add(fileInfo);
            }
        }
    }

    private boolean refreshAllPicCategory(FileCategory fc, Uri uri) {
        if (isRun && categoryHelper != null) {
            getAllPic(context, SortMethod.date);

            if (allPicsMap != null) {
                setCategoryInfo(fc, allPicCount, allPicSize);
            }
        }

        return false;
    }

    public HashMap<FileCategory, CategoryInfo> getCategoryInfos() {
        return mCategoryInfo;
    }

    public void setCategoryInfo(FileCategory fc, long count, long size) {
        if (mCategoryInfo != null) {
            CategoryInfo info = mCategoryInfo.get(fc);
            if (info == null) {
                info = new CategoryInfo();
                mCategoryInfo.put(fc, info);
            }
            info.count = count;
            info.size = size;
        }
    }


    /**
     * 得到我的相册图片
     *
     * @param context
     * @return
     */
    private void getAllPic(Context context, SortMethod sortMethod) {
        if (allPicsMap == null) {
            allPicsMap = new HashMap<String, ArrayList<FileInfo>>();
        }

        if (allPicsList == null) {
            allPicsList = new ArrayList<FileInfo>();
        }

        if (allPicDirNames == null) {
            allPicDirNames = new ArrayList<String>();
        }

        allPicsMap.clear();
        allPicDirNames.clear();
        allPicCount = 0;
        allPicSize = 0;

        String sort = categoryHelper.buildSortOrder(sortMethod);
        String[] projection = {Images.ImageColumns._ID, Images.ImageColumns.DATA, Images.ImageColumns.BUCKET_ID, Images.ImageColumns.BUCKET_DISPLAY_NAME,
                Images.ImageColumns.SIZE, Images.ImageColumns.TITLE, Images.ImageColumns.DATE_MODIFIED, Images.ImageColumns.MIME_TYPE};
        mPicC = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, sort);
        if (mPicC != null && mPicC.getCount() > 0) {
            int idd = mPicC.getColumnIndex(Images.ImageColumns._ID);
            int pathIndex = mPicC.getColumnIndex(Images.ImageColumns.DATA);
            int dirNameIndex = mPicC.getColumnIndex(Images.ImageColumns.BUCKET_DISPLAY_NAME);
            int size = mPicC.getColumnIndex(Images.ImageColumns.SIZE);
            int title = mPicC.getColumnIndex(Images.ImageColumns.TITLE);
            int mime = mPicC.getColumnIndex(Images.ImageColumns.MIME_TYPE);
            mPicC.moveToPosition(-1);
            while (mPicC.moveToNext()) {
                // String bulketID = c.getString(id);
                if (isUserStop) break;
                String bulketName = mPicC.getString(dirNameIndex);
                String path = mPicC.getString(pathIndex);
                int fileId = mPicC.getInt(idd);
                long filesize = mPicC.getLong(size);
                String fileTitle = mPicC.getString(title);
                String mimeType = mPicC.getString(mime);
                if (TextUtils.isEmpty(path)) {
                    continue;
                } else {
                    File myFile = new File(path);
                    if (!myFile.exists()) {
                        continue;
                    }
                }
                // 图片所属相册名字
                String albumPicName = getFName(context, path, bulketName);

                FileInfo info = new FileInfo();
                info.fileId = fileId;
                info.fileName = fileTitle;
                info.fileSize = filesize;
                info.filePath = path;
                info.mimeType = mimeType;
                info.fc = FileCategory.Picture;
                allPicCount++;
                allPicSize = allPicSize + filesize;

                if (MediaTypeForCamera.belongPicture(albumPicName)) {
                    ArrayList<FileInfo> photo = allPicsMap.get(FileBrowserUtil.PIC_DIR_PHOTO);
                    if (photo == null) {
                        photo = new ArrayList<FileInfo>();
                        allPicsMap.put(FileBrowserUtil.PIC_DIR_PHOTO, photo);
                    }

                    photo.add(info);
                } else if (MediaTypeForCamera.belongShotCut(albumPicName)) {
                    ArrayList<FileInfo> screenShot = allPicsMap.get(FileBrowserUtil.PIC_DIR_SCREENSHOTS);
                    if (screenShot == null) {
                        screenShot = new ArrayList<FileInfo>();
                        allPicsMap.put(FileBrowserUtil.PIC_DIR_SCREENSHOTS, screenShot);
                    }

                    screenShot.add(info);
                } else if (MediaTypeForCamera.belongQQFile_Recv(albumPicName)) {
                    ArrayList<FileInfo> qq = allPicsMap.get(FileBrowserUtil.PIC_DIR_QQ);
                    if (qq == null) {
                        qq = new ArrayList<FileInfo>();
                        allPicsMap.put(FileBrowserUtil.PIC_DIR_QQ, qq);
                    }

                    qq.add(info);
                } else {
                    ArrayList<FileInfo> other = allPicsMap.get(bulketName);
                    if (other == null) {
                        other = new ArrayList<FileInfo>();
                        allPicsMap.put(bulketName, other);
                        allPicDirNames.add(bulketName);
                    }

                    other.add(info);
                }
                allPicsList.add(info);
            }
            mPicC.close();

            ArrayList<FileInfo> qq = allPicsMap.get(FileBrowserUtil.PIC_DIR_QQ);
            if (qq != null) {
                allPicDirNames.add(0, FileBrowserUtil.PIC_DIR_QQ);
            }

            ArrayList<FileInfo> ss = allPicsMap.get(FileBrowserUtil.PIC_DIR_SCREENSHOTS);
            if (ss != null) {
                allPicDirNames.add(0, FileBrowserUtil.PIC_DIR_SCREENSHOTS);
            }

            ArrayList<FileInfo> ph = allPicsMap.get(FileBrowserUtil.PIC_DIR_PHOTO);
            if (ph != null) {
                allPicDirNames.add(0, FileBrowserUtil.PIC_DIR_PHOTO);
            }

            setCategoryInfo(FileCategory.Picture, allPicCount, allPicSize);
        }


        String wxPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MediaTypeForCamera.getWXPath();

        File dir = getWXImage2Dir(new File(wxPath));
        if (dir != null && dir.exists()) {
            getWXPic(dir);

            setCategoryInfo(FileCategory.Picture, allPicCount, allPicSize);
        }
    }

    public HashMap<String, ArrayList<FileInfo>> getAllPicMap() {
        if (allPicsMap == null) {
            getAllPic(context, SortMethod.date);
        }

        return allPicsMap;
    }

    public ArrayList<FileInfo> getAllPicList() {
        if (allPicsMap == null) {
            allPicsMap = new HashMap<String, ArrayList<FileInfo>>();
        } else {
            allPicsMap.clear();
        }

        if (allPicsList == null) {
            allPicsList = new ArrayList<FileInfo>();
        } else {
            allPicsList.clear();
        }


        if (allPicDirNames == null) {
            allPicDirNames = new ArrayList<String>();
        } else {
            allPicDirNames.clear();
        }
        getAllPic(context, SortMethod.date);
        return allPicsList;
    }

    public ArrayList<String> getAllPicDirNames() {
        if (allPicDirNames == null) {
            getAllPic(context, SortMethod.date);
        }

        return allPicDirNames;
    }

    public ArrayList<FileInfo> getAllMusic(Context context, SortMethod sortMethod) {
        String sort = categoryHelper.buildSortOrder(sortMethod);
        ArrayList<FileInfo> list = new ArrayList<FileInfo>();
        String[] projection = {Audio.AudioColumns.DATA, Audio.Media._ID, Audio.AudioColumns.DISPLAY_NAME, Audio.AudioColumns.SIZE,
                Audio.AudioColumns.DURATION, Audio.AudioColumns.MIME_TYPE, Audio.AudioColumns.TITLE};
        /*MediaStore.Audio.Media.IS_MUSIC + "=1"*/
        mMusicC = context.getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI, projection, null/*MediaStore.Audio.Media.IS_MUSIC + "=1"*/, null, sort);

        if (mMusicC != null && mMusicC.getCount() > 0) {
            mMusicC.moveToPosition(-1);
            int id = mMusicC.getColumnIndex(Audio.Media._ID);
            int title = mMusicC.getColumnIndex(Audio.AudioColumns.DISPLAY_NAME);
            int title2 = mMusicC.getColumnIndex(Audio.AudioColumns.TITLE);
            int duration = mMusicC.getColumnIndex(Audio.AudioColumns.DURATION);
            int size = mMusicC.getColumnIndex(Audio.AudioColumns.SIZE);
            int mine = mMusicC.getColumnIndex(Audio.AudioColumns.MIME_TYPE);
            int data = mMusicC.getColumnIndex(Audio.AudioColumns.DATA);
            while (mMusicC.moveToNext()) {
                String path = mMusicC.getString(data);

                if (path == null) {
                    continue;
                } else {
                    File mFile = new File(path);
                    if (!mFile.exists()) {
                        continue;
                    }
                }

                FileInfo info = new FileInfo();
                info.fileId = mMusicC.getInt(id);
                info.fileName = mMusicC.getString(title);
                if (TextUtils.isEmpty(info.fileName)) {
                    info.fileName = mMusicC.getString(title2);
                }
                info.duration = (int) mMusicC.getLong(duration);
                info.fileSize = mMusicC.getLong(size);
                info.mimeType = mMusicC.getString(mine);
                info.filePath = path;
                info.fc = FileCategory.Music;
                list.add(info);

            }
            mMusicC.close();
        }
        return list;
    }

    public ArrayList<FileInfo> getAllVideos(Context context, SortMethod sortMethod) {
        String sort = categoryHelper.buildSortOrder(sortMethod);
        ArrayList<FileInfo> list = new ArrayList<FileInfo>();
        String[] projection = {Video.VideoColumns.DATA, Video.Media._ID, Video.VideoColumns.DISPLAY_NAME, Video.VideoColumns.SIZE,
                Video.VideoColumns.DURATION, Video.VideoColumns.MIME_TYPE};
        mVideoC = context.getContentResolver().query(Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sort);
        if (mVideoC != null && mVideoC.getCount() > 0) {
            mVideoC.moveToPosition(-1);
            int id = mVideoC.getColumnIndex(Video.Media._ID);
            int title = mVideoC.getColumnIndex(Video.VideoColumns.DISPLAY_NAME);
            int duration = mVideoC.getColumnIndex(Video.VideoColumns.DURATION);
            int size = mVideoC.getColumnIndex(Video.VideoColumns.SIZE);
            int mine = mVideoC.getColumnIndex(Video.VideoColumns.MIME_TYPE);
            int data = mVideoC.getColumnIndex(Video.VideoColumns.DATA);
            while (mVideoC.moveToNext()) {
//                File mFile = new File(c.getString(data));
//                if (!mFile.exists())
//                    continue;
                FileInfo info = new FileInfo();
                info.fileId = mVideoC.getInt(id);
                info.fileName = mVideoC.getString(title);
                info.duration = (int) mVideoC.getLong(duration);
                info.fileSize = mVideoC.getLong(size);
                info.mimeType = mVideoC.getString(mine);
                info.filePath = mVideoC.getString(data);
                info.fc = FileCategory.Video;
                list.add(info);
            }
            mVideoC.close();
        }
        return list;
    }

    public ArrayList<FileInfo> getAllDoc(SortMethod sort) {
        refreshMediaCategory2();
        return docList;
    }

    public ArrayList<FileInfo> getAllApk(SortMethod sort) {
        refreshMediaCategory2();
        return apkList;
    }

    public ArrayList<FileInfo> getAllZip(SortMethod sort) {
        refreshMediaCategory2();
        return zipList;
    }


    private FileInfo getFileInfo(String path) {
        return (path == null || path.equals("")) ? null : FileBrowserUtil.GetFileInfo(path, 0);
    }

    private String getFName(Context context, String path, String bucket_display_name) {
        try {
            String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (TextUtils.isEmpty(sdcard)) {
                return "sdcard";
            }

            if (bucket_display_name.equals(sdcard)) {
                return "sdcard";
            }

            String temp = sdcard + "/";
            int posi = path.lastIndexOf(temp);
            if (posi == -1) {
                try {
                    if (Build.VERSION.SDK_INT >= 9) {
                        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
                        String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths").invoke(sm);
                        if (paths != null && paths.length > 0) {
                            for (int i = 0; i < paths.length; i++) {
                                posi = path.lastIndexOf(paths[i] + "/");
                                if (posi != -1) {
                                    sdcard = paths[i];
                                    break;
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            if (posi == -1) {
                return "sdcard";
            }

            int posiX = path.lastIndexOf(bucket_display_name + "/");
            if (posiX == -1) {
                return bucket_display_name;
            }

            if (posiX == (posi + 2 + sdcard.length())) {
                return bucket_display_name;
            } else {
                String prefix = path.substring(posi + 1 + sdcard.length(), posiX) + bucket_display_name;
                return prefix;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "sdcard";
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void close() {
        if (mVideoC != null && !mVideoC.isClosed()) {
            mVideoC.close();
        }
        if (mMusicC != null && !mMusicC.isClosed()) {
            mMusicC.close();
        }
        if (mPicC != null && !mPicC.isClosed()) {
            mPicC.close();
        }
        if (mMediaC != null && !mMediaC.isClosed()) {
            mMediaC.close();
        }
        isRun = false;
        isUserStop = false;
        deleteObservers();
        instance = null;
    }

}
