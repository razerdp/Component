package com.razerdp.router.core;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.razerdp.baselib.interfaces.SimpleCallback;
import com.razerdp.baselib.utils.ToolUtil;
import com.razerdp.router.core.define.ModuleDefine;
import com.razerdp.router.core.utils.ClassUtils;
import com.razerdp.router.core.utils.ModuleUtils;
import com.razerdp.router.core.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：module services单例，用于获取各个modules暴露的服务
 */
public enum ModuleManager {
    INSTANCE;
    private static final String TAG = "ModuleManager";
    //services的实例缓存
    private Map<Class, SparseArray<Object>> routerImplCacheMap = new HashMap<>();

    protected void init(Context context) {
        routerImplCacheMap.clear();
        boolean needUpdate = ModuleUtils.isNewVersion(context);
        if (needUpdate) {
            scan(context);
            ModuleUtils.updateVersion(context);
        } else {
            long startInit = System.currentTimeMillis();
            Set<String> mapping = SPUtils.getPreference().getStringSet(ModuleDefine.KEY_SERVICE_IMPL_CACHE, null);
            if (mapping != null) {
                attach(mapping);
            } else {
                scan(context);
            }
            Log.i(TAG, "init duration >> " + (System.currentTimeMillis() - startInit) + " ms");
        }

    }

    private void scan(Context context) {
        try {
            long startInit = System.currentTimeMillis();
            Set<String> mapping = ClassUtils.getFileNameByPackageName(context, ModuleDefine.ROOT_PAKCAGE);

            if (!mapping.isEmpty()) {
                SPUtils.getEditor().putStringSet(ModuleDefine.KEY_SERVICE_IMPL_CACHE, mapping).apply();
            }
            attach(mapping);
            Log.i(TAG, "init duration >> " + (System.currentTimeMillis() - startInit) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("组件初始化失败");
        }
    }

    private void attach(Set<String> from) {
        for (String s : from) {
            Object object = create(s);
            if (object instanceof IModuleServiceProvider) {
                HashMap<Class, SparseArray<Object>> map = ((IModuleServiceProvider) object).getMap();
                if (map != null && !map.isEmpty()) {
                    routerImplCacheMap.putAll(map);
                }
            }
        }
    }

    public <T> T getService(@NonNull Class<T> clazz) {
        return getService(clazz, 0);
    }

    @Nullable
    public <T> T getService(@NonNull Class<T> clazz, int tag) {
        T result = null;
        SparseArray<Object> routers = routerImplCacheMap.get(clazz);
        if (routers == null) {
            Log.e(TAG, "没有找到对应组件：" + clazz);
            return null;
        }
        result = ToolUtil.cast(routers.get(tag), clazz);
        return result;
    }

    public <T> void safeRun(@NonNull Class<T> clazz, @NonNull SimpleCallback<T> callback) {
        safeRun(clazz, 0, callback);
    }

    public <T> void safeRun(@NonNull Class<T> clazz, int tag, @NonNull SimpleCallback<T> callback) {
        T proxy = getService(clazz, tag);
        if (proxy == null) return;
        callback.onCall(proxy);
    }

    private Object create(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
