package com.ymnet.onekeyclean.cleanmore.filebrowser.lazyload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader
{
    
    public static final int MAXWIDTH = 480;
    
    public static final int MAXHEIGHT = 800;
    
    public enum MEDIATYPE
    {
        IMAGE, VIDEO, NOTCHCHKREUSE, BACKGROUND, RORATE, NORMAL
    };
    
    private MemoryCache memoryCache;
    
    private FileCache fileCache;
    
    private Map<View, String> imageViews;
    
    private ExecutorService executorService;
    
    Handler handler = new Handler();// handler to display images in UI thread
    
    private Context context;
    
    private Options options;
    
    private static ImageLoader imageLoader;
    
    public synchronized static ImageLoader getInstance(Context context)
    {
        if (imageLoader == null)
        {
            imageLoader = new ImageLoader(context);
        }
        return imageLoader;
    }
    
    private ImageLoader(Context context)
    {
        fileCache = new FileCache(context);
        options = new Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        this.context = context.getApplicationContext();
        memoryCache = new MemoryCache();
        imageViews = Collections.synchronizedMap(new WeakHashMap<View, String>());
        executorService = Executors.newFixedThreadPool(2);
    }
    
    /**
     * cursor图片
     */
    public void DisplayImage(int id, ImageView imageView, int defaultIcon, int errorIcon, int defaultX, int defaultY, MEDIATYPE type)
    {
        
        String url = null;
        if (type.equals(MEDIATYPE.IMAGE))
        {
            url = "I" + id;
        }
        else if (type.equals(MEDIATYPE.VIDEO))
        {
            url = "V" + id;
        }
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            if (defaultIcon > 0)
            {
                imageView.setImageResource(defaultIcon);
            }
            queuePhoto(new PhotoToLoad(url, imageView, id, defaultIcon, errorIcon, defaultX, defaultY, type));
        }
    }
    
    /**
     * 加载图片:
     * 
     * @param url
     * @param imageView
     * @param defaultIcon
     *            默认图标
     * @param errorIcon
     *            错误图标
     * @param defaultX
     *            默认宽度
     * @param defaultY
     *            默认高度
     */
    public void DisplayImage(String url, ImageView imageView, int defaultIcon, int errorIcon, int defaultX, int defaultY, MEDIATYPE type)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
        {
            if (type.equals(MEDIATYPE.RORATE))
            {
                Bitmap rote = createRotateBitmap(bitmap);
                memoryCache.put(url, rote);
                imageView.setImageBitmap(rote);
            }
            else
            {
                imageView.setImageBitmap(bitmap);
            }
        }
        else
        {
            if (defaultIcon > 0)
            {
                imageView.setImageResource(defaultIcon);
            }
            queuePhoto(new PhotoToLoad(url, imageView, -1, defaultIcon, errorIcon, defaultX, defaultY, type));
        }
    }
    
    private Bitmap createRotateBitmap(Bitmap bitmap)
    {
        if (bitmap.getWidth() > bitmap.getHeight())
        {
            Matrix matrix = new Matrix();
            // 缩放原图
            matrix.postRotate(90);
            // bmp.getWidth(), 500分别表示重绘后的位图宽高
            try
            {
                Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return dstbmp;
            }
            catch (Exception e)
            {
                return bitmap;
            }
        }
        else
        {
            return bitmap;
        }
    }
    
    private void queuePhoto(PhotoToLoad p)
    {
        if (null == executorService)
        {
            executorService = Executors.newFixedThreadPool(2);
        }
        else if (executorService.isShutdown() || executorService.isTerminated())
        {
            executorService = null;
            executorService = Executors.newFixedThreadPool(2);
        }
        executorService.submit(new PhotosLoader(p));
    }
    
    class PhotosLoader implements Runnable
    {
        PhotoToLoad photoToLoad;
        
        PhotosLoader(PhotoToLoad photoToLoad)
        {
            this.photoToLoad = photoToLoad;
        }
        
        @Override
        public void run()
        {
            Bitmap bmp = null;
            try
            {
                if (!photoToLoad.type.equals(MEDIATYPE.NOTCHCHKREUSE))
                {
                    if (imageViewReused(photoToLoad))
                        return;
                }
                bmp = getBitmap(photoToLoad);
                if (null != bmp)
                {
                    memoryCache.put(photoToLoad.url, bmp);
                }
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
            finally
            {
                if (!photoToLoad.type.equals(MEDIATYPE.NOTCHCHKREUSE))
                {
                    if (imageViewReused(photoToLoad))
                        return;
                }
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                if (null == handler)
                {
                    handler = new Handler();
                }
                handler.post(bd);
            }
        }
    }
    
    private Bitmap getBitmap(PhotoToLoad photoToLoad)
    {
        if (photoToLoad.type.equals(MEDIATYPE.IMAGE))
        {
            return Images.Thumbnails.getThumbnail(context.getContentResolver(), photoToLoad.id, Images.Thumbnails.MINI_KIND, options);
        }
        else if (photoToLoad.type.equals(MEDIATYPE.VIDEO))
        {
            return MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), photoToLoad.id, MediaStore.Video.Thumbnails.MINI_KIND, options);
        }
        String url = photoToLoad.url;
        File f = fileCache.checkByHasecode(url);
        
        if (f != null && f.exists())
        { // from SD cache
            Bitmap b = decodeFile(photoToLoad, f);
            f.setLastModified(System.currentTimeMillis());
            if (b != null)
            {
                return b;
            }
        }
        
        // from web
        Bitmap bitmap = null;
        try
        {
            //for most app icon small so bytearraybuffer
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            int count = conn.getContentLength();
            f = fileCache.generatorByHashcode(url, count);
            if (0 < count && count < 50 * 1024)
            {
                byte[] datas = new byte[count];
                int offset = 0;
                int length = count;
                int readLength;
                while ((readLength = is.read(datas, offset, length)) != -1)
                {
                    offset += readLength;
                    length -= readLength;
                    if (length == 0)
                    {
                        break;
                    }
                }
                bitmap = decodeFile(photoToLoad, datas);
                is.close();
                OutputStream os = new FileOutputStream(f);
                os.write(datas, 0, datas.length);
                os.flush();
                os.close();
                datas = null;
            }
            else
            {
                OutputStream os = new FileOutputStream(f);
                BitmapUtils.CopyStream(is, os);
                is.close();
                os.close();
                conn.disconnect();
                bitmap = decodeFile(photoToLoad, f);
            }
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)//TODO not hold catch
            {
                memoryCache.clear();
            }
            else if (ex instanceof IOException)
            {
                
            }
        }
        return bitmap;
    }
    
    private Bitmap decodeFile(PhotoToLoad photoToLoad, byte[] data)
    {
        Bitmap bitmap = null;
        try
        {
            // decode image size
            Options o = new Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, o);
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true)
            {
                //判断图片大小是否大于iamgeView的大小
                if (width_tmp <= (Math.min(photoToLoad.width, MAXWIDTH)) && height_tmp <= (Math.min(photoToLoad.height, MAXHEIGHT)))
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            Options o2 = new Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, o2);
            if (photoToLoad.type.equals(MEDIATYPE.RORATE))
            {
                Bitmap rote = createRotateBitmap(bitmap);
                bitmap = rote;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }
    
    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(PhotoToLoad photoToLoad, File f)
    {
        Bitmap bitmap = null;
        try
        {
            // decode image size
            Options o = new Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
            
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true)
            {
                //判断图片大小是否大于iamgeView的大小
                if (width_tmp <= (Math.min(photoToLoad.width, MAXWIDTH)) && height_tmp <= (Math.min(photoToLoad.height, MAXHEIGHT)))
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            Options o2 = new Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            if (photoToLoad.type.equals(MEDIATYPE.RORATE))
            {
                Bitmap rote = createRotateBitmap(bitmap);
                bitmap = rote;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad)
    {
        if (null != photoToLoad.background)
        {
            return false;
        }
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
        {
            return true;
        }
        return false;
    }
    
    // Task for the queue
    private class PhotoToLoad
    {
        public String url;
        
        public ImageView imageView;
        
        public int width;
        
        public int height;
        
        public View background;
        
        public int id;
        
        public MEDIATYPE type = MEDIATYPE.NORMAL;
        
        public int errorIcon;
        
        public PhotoToLoad(String u, ImageView i, int id, int defaultIcon, int errorIcon, int defaultX, int defaultY, MEDIATYPE type)
        {
            this.id = id;
            this.type = type;
            url = u;
            imageView = i;
            width = defaultX;
            height = defaultY;
            this.errorIcon = errorIcon;
        }
        
        public PhotoToLoad(String u, View i, int id, int defaultIcon, int errorIcon, int defaultX, int defaultY, MEDIATYPE type)
        {
            this.id = id;
            this.type = type;
            url = u;
            background = i;
            width = defaultX;
            height = defaultY;
            this.errorIcon = errorIcon;
        }
    }
    
    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        
        PhotoToLoad photoToLoad;
        
        public BitmapDisplayer(Bitmap b, PhotoToLoad p)
        {
            bitmap = b;
            photoToLoad = p;
        }
        
        public void run()
        {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
            {
                if (!photoToLoad.type.equals(MEDIATYPE.BACKGROUND))
                {
                    photoToLoad.imageView.setImageBitmap(bitmap);
                }
                else
                {
                    photoToLoad.background.setBackgroundDrawable(new BitmapDrawable(bitmap));
                }
            }
            else
            {
                if (!photoToLoad.type.equals(MEDIATYPE.BACKGROUND))
                {
                    if (photoToLoad.errorIcon > 0)
                    {
                        photoToLoad.imageView.setImageResource(photoToLoad.errorIcon);
                    }
                }
                else
                {
                    if (photoToLoad.errorIcon > 0)
                    {
                        photoToLoad.background.setBackgroundResource(photoToLoad.errorIcon);
                    }
                }
                
            }
            photoToLoad = null;
        }
    }
    
    public void clearCache()
    {
        memoryCache.clear();
    }
}