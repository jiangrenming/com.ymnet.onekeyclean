package com.ymnet.onekeyclean.cleanmore.constants;


/**
 * 清理状态
 * 
 */
public class ScanState {
	public static final int SCANING_PATH=0;//正在扫描中 带路径
	public static final int SCANING_SYSTEM_CACHE_END=1;
	public static final int SCANING_APP_CACHE_END=2;
    public static final int SCANING_RESIDUAL_END=6;
	public static final int SCANING_APK_FILE_END=3;
    public static final int SCAN_RAM_END=4;
	public static final int SCAN_ALL_END=5;//扫描全部结束
	
	public static final int DELING_PATH=10;//正在删除 删除的路径
	public static final int DEL_SYSTEM_CACHE_END=11;//删除系统缓存结束
	public static final int DEL_APP_CACHE_END=12;//删除app缓存结束
	public static final int DEL_RESIDUAL_END=13;//删除app残留结束
	public static final int DEL_APK_FILE_END=14;//删除apk文件结束
	public static final int DEL_ALL_END=15;//删除全部结束

}

