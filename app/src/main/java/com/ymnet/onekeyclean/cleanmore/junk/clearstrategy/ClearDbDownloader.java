package com.ymnet.onekeyclean.cleanmore.junk.clearstrategy;

import android.content.Context;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: yangning
 * Date: 1/6/15
 * To change this template use File | Settings | File Templates.
 */
public class ClearDbDownloader {
    private static final String TAG = "ClearDbDownloader";
    private Context mContext;
    ClearDbDownloadListener clearDbDownloadListener;
    private File tempFile;

    public void setClearDbDownloadListener(ClearDbDownloadListener clearDbDownloadListener) {
        this.clearDbDownloadListener = clearDbDownloadListener;
    }

    public ClearDbDownloader(Context context) {
        mContext = context;
        this.tempFile = getTmpFile();
    }

   /* public void downloadDb(String downloadUrl, final File tempFile, final String md5) {
        if (tempFile != null) {
            this.tempFile = tempFile;
        }
        if (NetworkUtils.isWifiNetwork(mContext) || SecurityAppInfo.isEmulator(mContext)) {
            DownloadManager.getInstance(C.get()).downloadFile(downloadUrl, this.tempFile.getAbsolutePath(), new DownloadFileCallback() {
                @Override
                public void onDownloadStart() {
                }

                @Override
                public void onDownloading(long total, long current) {
                }

                @Override
                public void onDownloadFailure(int errStatus, String errMsg) {
                    clearDbDownloadListener.failed();
                }

                @Override
                public void onDownloadSuccess(String path) {
                    if (clearDbDownloadListener != null) {
                        clearDbDownloadListener.finish(tempFile, md5);
                    }
                }
            });
        }
    }*/

    private File getTmpFile() {
        return new File(mContext.getFilesDir().getPath(), "temp.temp");
    }

    public interface ClearDbDownloadListener {
        public void finish(File resultFile, String md5);

        public void failed();
    }

    /*private boolean downloadClearDb(String url, File tempFile) {
        final HttpClient client = (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) ? new DefaultHttpClient()
                : AndroidHttpClient.newInstance("Android");
        final HttpGet httpGet = new HttpGet(url);
        InputStream inputStream = null;
        FileOutputStream fos = null;
        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(httpGet);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                entity = response.getEntity();
                inputStream = entity.getContent();

                if (this.tempFile.exists()) {
                    this.tempFile.delete();
                }
                this.tempFile.getParentFile().mkdirs();
                this.tempFile.createNewFile();
                fos = new FileOutputStream(this.tempFile);
                byte[] buf = new byte[8096];
                int read = -1;
                while ((read = inputStream.read(buf)) != -1) {
                    fos.write(buf, 0, read);
                }
                return true;
            } else {
                LogTag.debug(TAG, "statusCode--->" + statusCode);
            }
        } catch (IOException e) {
            LogTag.debug(TAG, "IOException--->" + e);
        } catch (Exception e) {
            LogTag.debug(TAG, "Exception--->" + e);
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            httpGet.abort();
            if (client instanceof AndroidHttpClient) {
                ((AndroidHttpClient) client).close();
            }
        }
        return false;
    }*/

}
