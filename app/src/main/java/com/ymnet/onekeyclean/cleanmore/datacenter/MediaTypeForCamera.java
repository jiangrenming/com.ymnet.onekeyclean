package com.ymnet.onekeyclean.cleanmore.datacenter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MediaTypeForCamera
{
    
    private static Map<String, String> SOPPORT_IMAGE = new HashMap<>();
    
    private static final String QQ_1 = "tencent/QQfile_recv";
    
    private static final String QQ_2 = "Tencent/QQfile_recv";
    
    private static final String WX_1 = "tencent/MicroMsg";
    
    private static final String WX_2 = "Tencent/MicroMsg";
    
    private static Map<String, String> SHOT = new HashMap<>();
    
    private static Map<String, String> PICTURE = new HashMap<>();
    
    private static Map<String, String> REVICE = new HashMap<>();
    
    private static Map<String, String> VIDEO = new HashMap<>();
    
    private static Map<String, String> WALLPAPER = new HashMap<>();
    
    private static Map<String, String> VIDEOSUFFIX = new HashMap<>();
    
    private static Map<String, String> MUSICSUFFIX = new HashMap<>();
    
    private static Map<String, String> RINGSUFFIX = new HashMap<>();
    
    private static Map<String, String> IMAGESUFFIX = new HashMap<>();
    
    static
    {
        IMAGESUFFIX.put(".png", "image/png");
        IMAGESUFFIX.put(".jpeg", "image/jpeg");
        IMAGESUFFIX.put(".jpg", "image/jpeg");
        IMAGESUFFIX.put(".gif", "image/gif");
        IMAGESUFFIX.put(".wbmp", "image/wbmp");
        IMAGESUFFIX.put(".bmp", "image/wbmp");
    }
    static
    {
        VIDEOSUFFIX.put(".mpeg", "video/mpeg");
        VIDEOSUFFIX.put(".mpg", "video/mpeg");
        VIDEOSUFFIX.put(".mp4", "video/mp4");
        VIDEOSUFFIX.put(".m4v", "video/mp4");
        VIDEOSUFFIX.put(".3gp", "video/3gpp");
        VIDEOSUFFIX.put(".3gpp", "video/3gpp");
        VIDEOSUFFIX.put(".avi", "video/avi");
    }

    static
    {
        MUSICSUFFIX.put(".mp3", "audio/mpeg");
        MUSICSUFFIX.put(".wav", "audio/x-wav");
        MUSICSUFFIX.put(".amr", "audio/amr");
        MUSICSUFFIX.put(".awb", "audio/amr-wb");
        MUSICSUFFIX.put(".ogg", "application/ogg");
        MUSICSUFFIX.put(".oga", "application/ogg");
        MUSICSUFFIX.put(".aac", "audio/aac");
    }
    static
    {
        RINGSUFFIX.put(".bmp", "");
        RINGSUFFIX.put(".gif", "");
        RINGSUFFIX.put(".jpg", "");
        RINGSUFFIX.put(".jpeg", "");
        RINGSUFFIX.put(".png", "");
        RINGSUFFIX.put(".wbmp", "");
    }
    static
    {
        SOPPORT_IMAGE.put(".bmp", "");
        SOPPORT_IMAGE.put(".gif", "");
        SOPPORT_IMAGE.put(".jpg", "");
        SOPPORT_IMAGE.put(".jpeg", "");
        SOPPORT_IMAGE.put(".png", "");
        SOPPORT_IMAGE.put(".wbmp", "");
    }
    
    static
    {
        WALLPAPER.put("Wallpaper", "");
        WALLPAPER.put("WallPaper", "");
        WALLPAPER.put("wallpaper", "");
        WALLPAPER.put("2345手机助手/wallpaper", "");
    }
    static
    {
    	SHOT.put("DCIM/Screenshots", "");
    	SHOT.put("dcim/Screenshots", "");
    	SHOT.put("Screenshots", "");
        SHOT.put("Pictures/Screenshots", "");
        SHOT.put("ScreenCapture", "");
        SHOT.put("Coolpad/截屏图片", "");
        SHOT.put("Photo/Screenshots", "");
    }
    static
    {
    	PICTURE.put("dcim/camera", "");
        PICTURE.put("dcim/camera/multishoot", "");
        PICTURE.put("dcim/100andro", "");
        PICTURE.put("dcim/100media", "");
        PICTURE.put("camera", "");
        PICTURE.put("dcim", "");
        PICTURE.put("pictures/camera", "");
        PICTURE.put("我的相机", "");
        PICTURE.put("相机/照片", "");
        PICTURE.put("照相机/camera", "");
        PICTURE.put("2345手机助手/photo", "");
        PICTURE.put("相机","");
    }
    static
    {
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
        REVICE.put("", "");
    }
    static
    {
        VIDEO.put("DCIM/Camera", "");
        VIDEO.put("camera", "");
        VIDEO.put("DCIM", "");
        VIDEO.put("Camera", "");
        VIDEO.put("Camera/Video", "");
        VIDEO.put("Pictures/Camera", "");
        VIDEO.put("我的相机", "");
        VIDEO.put("Photo", "");
        VIDEO.put("camera/Pictures", "");
        VIDEO.put("我的照片", "");
        VIDEO.put("相机/照片", "");
        VIDEO.put("照相机/Camera", "");
        VIDEO.put("video", "");
        VIDEO.put("camera/Videos", "");
        VIDEO.put("我的视频", "");
        VIDEO.put("相机/录像", "");
    }
    
    public static boolean belongPicture(String dir)
    {
        if (PICTURE.containsKey(dir.toLowerCase(Locale.CHINA)))
        {
            return true;
        }

        return dir.toLowerCase(Locale.CHINA).startsWith("dcim/camera");

    }
    
    public static boolean belongShotCut(String dir)
    {
        return SHOT.containsKey(dir);
    }
    
    public static boolean belongQQFile_Recv(String dir) {
        return dir.startsWith(QQ_1) || dir.startsWith(QQ_2);
    }
    
    public static boolean belongWX(String dir) {
        return (dir.startsWith(WX_1) || dir.startsWith(WX_2)) && dir.contains("image2");
    }
    
    public static boolean belongVideo(String dir)
    {
        return VIDEO.containsKey(dir);
    }
    
    public static boolean belongReceive(String dir)
    {
        return REVICE.containsKey(dir);
    }
    
    public static boolean belongWallpaper(String dir)
    {
        return WALLPAPER.containsKey(dir);
    }
    
    public static boolean belongOtherImage(String dir) {
        return !belongWallpaper(dir) && !belongPicture(dir);

    }
    
    public static boolean supportImageType(String dir)
    {
        return !SOPPORT_IMAGE.containsKey(dir);
    }
    
    public static boolean supportMusicType(String dir)
    {
        return MUSICSUFFIX.containsKey(dir);
    }
    
    public static boolean supportVideoType(String dir)
    {
        return VIDEOSUFFIX.containsKey(dir);
    }
    
    public static boolean supportImage(String dir)
    {
        return IMAGESUFFIX.containsKey(dir);
    }
    
    public static String supportImageSuffix(String suffix)
    {
        return IMAGESUFFIX.get(suffix);
    }
    
    public static String supportMusicSuffix(String suffix)
    {
        return MUSICSUFFIX.get(suffix);
    }
    
    public static String supportVideoSuffix(String suffix)
    {
        return VIDEOSUFFIX.get(suffix);
    }
    
    public static String getWXPath()
    {
    	return WX_2;
    }
    
}
