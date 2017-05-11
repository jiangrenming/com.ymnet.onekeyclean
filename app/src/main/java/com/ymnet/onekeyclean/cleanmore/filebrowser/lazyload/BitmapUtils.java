package com.ymnet.onekeyclean.cleanmore.filebrowser.lazyload;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class BitmapUtils
{
    private static final String TAG = "BitmapUtils";
    
    private static final int COMPRESS_JPEG_QUALITY = 90;
    
    public static final int UNCONSTRAINED = -1;
    
    private BitmapUtils()
    {
    }
    
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try
        {
            byte[] bytes = new byte[buffer_size];
            for (;;)
            {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
                os.flush();
            }
        }
        catch (IOException ex)
        {
        }
    }
    
    public static Bitmap resizeDownToPixels(Bitmap bitmap, int targetPixels, boolean recycle)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = (float) Math.sqrt((double) targetPixels / (width * height));
        if (scale >= 1.0f)
            return bitmap;
        return resizeBitmapByScale(bitmap, scale, recycle);
    }
    
    public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale, boolean recycle)
    {
        int width = Math.round(bitmap.getWidth() * scale);
        int height = Math.round(bitmap.getHeight() * scale);
        if (width == bitmap.getWidth() && height == bitmap.getHeight())
            return bitmap;
        Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle)
            bitmap.recycle();
        return target;
    }
    
    private static Bitmap.Config getConfig(Bitmap bitmap)
    {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null)
        {
            config = Bitmap.Config.RGB_565;
        }
        return config;
    }
    
    public static Bitmap resizeDownBySideLength(Bitmap bitmap, int maxLength, boolean recycle)
    {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.min((float) maxLength / srcWidth, (float) maxLength / srcHeight);
        if (scale >= 1.0f)
            return bitmap;
        return resizeBitmapByScale(bitmap, scale, recycle);
    }
    
    // Resize the bitmap if each side is >= targetSize * 2
    public static Bitmap resizeDownIfTooBig(Bitmap bitmap, int targetSize, boolean recycle)
    {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.max((float) targetSize / srcWidth, (float) targetSize / srcHeight);
        if (scale > 0.5f)
            return bitmap;
        return resizeBitmapByScale(bitmap, scale, recycle);
    }
    
    // Crops a square from the center of the original image.
    public static Bitmap cropCenter(Bitmap bitmap, boolean recycle)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width == height)
            return bitmap;
        int size = Math.min(width, height);
        
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle)
            bitmap.recycle();
        return target;
    }
    
    public static Bitmap resizeDownAndCropCenter(Bitmap bitmap, int size, boolean recycle)
    {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int minSide = Math.min(w, h);
        if (w == h && minSide <= size)
            return bitmap;
        size = Math.min(size, minSide);
        
        float scale = Math.max((float) size / bitmap.getWidth(), (float) size / bitmap.getHeight());
        Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
        int width = Math.round(scale * bitmap.getWidth());
        int height = Math.round(scale * bitmap.getHeight());
        Canvas canvas = new Canvas(target);
        canvas.translate((size - width) / 2f, (size - height) / 2f);
        canvas.scale(scale, scale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle)
            bitmap.recycle();
        return target;
    }
    
    public static void recycleSilently(Bitmap bitmap)
    {
        if (bitmap == null)
            return;
        try
        {
            bitmap.recycle();
        }
        catch (Throwable t)
        {
            Log.w(TAG, "unable recycle bitmap", t);
        }
    }
    
    public static Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle)
    {
        if (rotation == 0)
            return source;
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(rotation);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
        if (recycle)
            source.recycle();
        return bitmap;
    }
    
    public static Bitmap createVideoThumbnail(String filePath)
    {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try
        {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();
            
            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);
            
            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9)
            {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            }
            else
            {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null)
                        return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        }
        catch (IllegalArgumentException ex)
        {
            // Assume this is a corrupt video file
        }
        catch (RuntimeException ex)
        {
            // Assume this is a corrupt video file.
        }
        catch (InstantiationException e)
        {
            Log.e(TAG, "createVideoThumbnail", e);
        }
        catch (InvocationTargetException e)
        {
            Log.e(TAG, "createVideoThumbnail", e);
        }
        catch (ClassNotFoundException e)
        {
            Log.e(TAG, "createVideoThumbnail", e);
        }
        catch (NoSuchMethodException e)
        {
            Log.e(TAG, "createVideoThumbnail", e);
        }
        catch (IllegalAccessException e)
        {
            Log.e(TAG, "createVideoThumbnail", e);
        }
        finally
        {
            try
            {
                if (instance != null)
                {
                    clazz.getMethod("release").invoke(instance);
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return null;
    }
    
    public static byte[] compressBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, COMPRESS_JPEG_QUALITY, os);
        return os.toByteArray();
    }
    
    public static boolean isSupportedByRegionDecoder(String mimeType)
    {
        if (mimeType == null)
            return false;
        mimeType = mimeType.toLowerCase(Locale.getDefault());
        return mimeType.startsWith("image/") && (!mimeType.equals("image/gif") && !mimeType.endsWith("bmp"));
    }
    
    public static boolean isRotationSupported(String mimeType)
    {
        if (mimeType == null)
            return false;
        mimeType = mimeType.toLowerCase(Locale.getDefault());
        return mimeType.equals("image/jpeg");
    }
    
    public static byte[] compressToBytes(Bitmap bitmap, int quality)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }
}
