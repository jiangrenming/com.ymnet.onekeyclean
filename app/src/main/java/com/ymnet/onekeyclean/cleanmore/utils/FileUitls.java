package com.ymnet.onekeyclean.cleanmore.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

/**
 * Created by duyp on 2016/1/14.
 */
public class FileUitls {
    private static final String DAEMON_INFO = "daemon";

    public static String getPath() {
        String path = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "一键清理" + File.separator + DAEMON_INFO + File.separator;
        } else {
            path = C.get().getFilesDir().getAbsolutePath() + File.separator + DAEMON_INFO + File.separator;
        }
        return path;
    }

    public static String getFilePath(String fileName){
        return FileUitls.getPath() + fileName;
    }

    public static void writeFile(String path, String info) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            bw.write(info + "\n\t");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }

                if (osw != null) {
                    osw.close();
                }

                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFile(File file) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String line = null;
        try {
            if (file.isFile() && file.exists()) {
                fis = new FileInputStream(file);
                fis.available();
                isr = new InputStreamReader(fis, "UTF-8");
                br = new BufferedReader(isr);
                line = br.readLine();
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }

                if (isr != null) {
                    isr.close();
                }

                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return line;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
