package com.razerdp.router.core;

import android.content.Context;

import androidx.annotation.NonNull;

import com.razerdp.common.modules.BaseModulesProvider;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
public class InitProvider extends BaseModulesProvider {
    @Override
    public void onInit(@NonNull Context context) {
        ModuleManager.INSTANCE.init(context);
    }
}
