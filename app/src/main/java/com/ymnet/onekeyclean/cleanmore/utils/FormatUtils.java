package com.ymnet.onekeyclean.cleanmore.utils;


import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by wangduheng26 on 4/5/16.
 */
public class FormatUtils {
    public static String[] getFileSizeAndUnit(long size) {
        if (size < 0) {
            size = 0;
        }
        String[] result = new String[2];
        String formatFileSize = FormatUtils.formatFileSize(size);
        if (formatFileSize.length() >= 6) {
            String strSize = formatFileSize.substring(0, 4);
            String strUnit = formatFileSize.substring(formatFileSize.length() - 2, formatFileSize.length());
            if (strSize.endsWith(".")) {
                strSize = strSize.replace(".", "");
            }
            result[0] = strSize;
            result[1] = strUnit;
        } else {
            result[0] = "0.00";
            result[1] = "KB";
        }
        return result;
    }

    public static String formatFileSize(long fileSize) {// 转换文件大小
        String fileSizeString = "";
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        if (fileSize == 0) {
            fileSizeString = "0.00KB";
        } else if (fileSize < 1000) {
            fileSizeString = decimalFormat.format((double) fileSize) + "B";
        } else if (fileSize < (1000 * 1024)) {
            fileSizeString = decimalFormat.format((double) fileSize / ByteConstants.KB) + "KB";
        } else if (fileSize < (1000 * 1024 * 1024)) {
            fileSizeString = decimalFormat.format((double) fileSize / (ByteConstants.MB)) + "MB";
        } else {
            fileSizeString = decimalFormat.format((double) fileSize / (ByteConstants.GB)) + "GB";
        }
        return fileSizeString;
    }

    public static String percent(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        str = nf.format(p3);
        return str;
    }

}
