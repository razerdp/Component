package com.razerdp.baselib.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;


/**
 * Created by 大灯泡 on 2019/4/19.
 */
public class VersionUtil {

    private static String versionName;
    private static long versionCode = -1;

    public static String getVersionName() {
        if (TextUtils.isEmpty(versionName)) {
            PackageManager packageManager = AppContext.getAppContext().getPackageManager();
            PackageInfo packInfo = null;
            try {
                packInfo = packageManager.getPackageInfo(AppContext.getAppContext().getPackageName(), 0);
                versionName = packInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                versionName = "";
            }
        }
        return versionName;
    }


    /**
     * 获取当前应用的版本号
     */
    public static long getVersionCode() {
        if (versionCode == -1) {
            PackageManager packageManager = AppContext.getAppContext().getPackageManager();
            PackageInfo packInfo;
            try {
                packInfo = packageManager.getPackageInfo(
                        AppContext.getAppContext().getPackageName(), 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    versionCode = packInfo.getLongVersionCode();
                }else {
                    versionCode = packInfo.versionCode;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                versionCode = -1;
            }
        }
        return versionCode;
    }
}
