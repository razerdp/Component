package com.razerdp.router.core.utils;

import android.content.Context;
import android.text.TextUtils;

import com.razerdp.baselib.utils.VersionUtil;
import com.razerdp.router.core.define.ModuleDefine;


/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
public class ModuleUtils {

    private static final String TAG = "ModuleUtils";

    public static boolean isNewVersion(Context context) {
        if (!SPUtils.containsKey(ModuleDefine.KEY_VERSION) ||
                !SPUtils.containsKey(ModuleDefine.KEY_VERSION_NAME)) {
            return true;
        }
        long versionCode = VersionUtil.getVersionCode();
        String versionName = VersionUtil.getVersionName();

        long cacheVersionCode = SPUtils.getLong(ModuleDefine.KEY_VERSION, -1);
        String cacheVersionName = SPUtils.getString(ModuleDefine.KEY_VERSION_NAME, null);

        return versionCode != cacheVersionCode || !TextUtils.equals(versionName, cacheVersionName);
    }

    public static void updateVersion(Context context) {
        long versionCode = VersionUtil.getVersionCode();
        String versionName = VersionUtil.getVersionName();

        SPUtils.saveLong(ModuleDefine.KEY_VERSION, versionCode);
        SPUtils.saveString(ModuleDefine.KEY_VERSION_NAME, versionName);
    }

}
