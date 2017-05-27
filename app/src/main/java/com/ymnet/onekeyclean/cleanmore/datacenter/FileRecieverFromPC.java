package com.ymnet.onekeyclean.cleanmore.datacenter;/*
package com.example.baidumapsevice.datacenter;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;

import com.example.baidumapsevice.wechat.MTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class FileRecieverFromPC {
    private static final String TAG = FileRecieverFromPC.class.getSimpleName();
    private int fileCount;

    private ArrayList<FileFromPC> files;

    Handler mHandler;

    Context context;

    ContentObserver mObserver;

    void init(Handler handler, Context context) {
        mHandler = handler;
        this.context = context;

        files = new ArrayList<FileFromPC>();
        fileCount = 0;
        fillData();
        mObserver = new ContentObserver(mHandler) {
            public void onChange(boolean selfChange) {
                fillData();
            }
        };
        try {
            context.getContentResolver().registerContentObserver(MarketProvider.RECEIVER_URL, true, mObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int getFileCount() {
        return fileCount;
    }

    synchronized ArrayList<FileFromPC> getFiles() {
        return (ArrayList<FileFromPC>) files.clone();
    }

    public void setFiles(ArrayList<FileFromPC> files) {
        this.files = files;
    }

    protected synchronized void fillData() {
        Cursor c = context.getContentResolver().query(MarketProvider.RECEIVER_URL, null, null, null, null);
        if (c == null) {
            return;
        }
        files.clear();
        List<Integer> ids = new ArrayList<Integer>();
        c.moveToPosition(-1);
        int count = 0;
        int id = c.getColumnIndex(MarketProvider.COLUMN_ID);
        int path = c.getColumnIndex("path");
        int name = c.getColumnIndex("filename");
        int modify = c.getColumnIndex("modify");
        int size = c.getColumnIndex("size");
        while (c.moveToNext()) {
            FileFromPC fp = new FileFromPC();
            fp.id = c.getInt(id);
            fp.fileName = c.getString(name);
            fp.filePath = c.getString(path);
            fp.fileSize = c.getLong(size);
            fp.lastModify = c.getLong(modify);
            if (!TextUtils.isEmpty(fp.filePath)) {
                File f = new File(fp.filePath);

                if (f.exists()) {
                    files.add(fp);
                    count++;
                } else {
                    ids.add(fp.id);
                }
            } else {
                ids.add(fp.id);
            }
        }
        fileCount = count;
        try {
            c.close();
        } catch (Exception e) {
        }
        mHandler.sendEmptyMessage(DataCenterObserver.RECEVIRE_FILE_FROM_PC);
        MediaBulkDeleter deleter = new MediaBulkDeleter(context, MarketProvider.RECEIVER_URL);
        for (Integer ida : ids) {
            deleter.delete(ida, null);
        }
        deleter.flush();
    }

    void deleteFileById(final int[] ids, final String[] files, final boolean deleFile) {
        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {

            @Override
            public void run() {
                if (deleFile) {
                    for (String path : files) {
                        if (TextUtils.isEmpty(path)) {
                            continue;
                        }
                        File f = new File(path);
                        if (f.exists()) {
                            f.delete();
                        }
                    }
                }
                MediaBulkDeleter deleter = new MediaBulkDeleter(context, MarketProvider.RECEIVER_URL);
                for (Integer ida : ids) {
                    deleter.delete(ida, null);
                }
                deleter.flush();
            }
        });
    }
}
*/
