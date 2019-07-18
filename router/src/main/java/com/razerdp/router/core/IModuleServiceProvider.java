package com.razerdp.router.core;

import android.util.SparseArray;

import java.util.HashMap;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：module生成文件提供
 */
public interface IModuleServiceProvider {
    HashMap<Class, SparseArray<Object>> getMap();
}
