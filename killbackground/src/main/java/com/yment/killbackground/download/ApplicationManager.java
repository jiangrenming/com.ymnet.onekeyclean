package com.yment.killbackground.download;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/9/19.
 */
public class ApplicationManager {
    public final int INSTALL_REPLACE_EXISTING = 2;
    public static final int INSTALL_SUCCEEDED = 1;

    private PackageManager pm;
    private Method method;

    public ApplicationManager(Context context) throws SecurityException, NoSuchMethodException {

        pm = context.getPackageManager();

        Class<?>[] types;
        try {
            types = new Class[] { Uri.class, Class.forName("android.content.pm.IPackageInstallObserver"), int.class, String.class };
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            types = new Class[] { Uri.class, Object.class, int.class, String.class };
        }
        method = pm.getClass().getMethod("installPackage", types);
    }

    public void installPackage(String apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        installPackage(new File(apkFile));
    }

    public void installPackage(File apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (!apkFile.exists()) throw new IllegalArgumentException();
        Uri packageURI = Uri.fromFile(apkFile);
        installPackage(packageURI);
    }

    public void installPackage(Uri apkFile) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        method.invoke(pm, new Object[] { apkFile, null, INSTALL_REPLACE_EXISTING, null });
    }

}