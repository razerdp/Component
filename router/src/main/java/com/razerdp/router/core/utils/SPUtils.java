package com.razerdp.router.core.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.razerdp.baselib.utils.AppContext;
import com.razerdp.router.core.define.ModuleDefine;

import java.util.Map;

/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
public class SPUtils {
    private static final String PREFERENCE_NAME = ModuleDefine.SP_NAME;
    private static SharedPreferences sharedPreferences;


    static {
        sharedPreferences = AppContext.getAppContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static String getString(String key, String defaultValue) {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.getString(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static Map<String, ?> getAll() {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.getAll();
    }

    public static int getInt(String key, int defaultValue) {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void saveString(String key, String value) {
        createSharedPreferencesIfNotExist();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveBoolean(String key, boolean value) {
        createSharedPreferencesIfNotExist();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveInt(String key, int value) {
        createSharedPreferencesIfNotExist();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void saveFloat(String key, float value) {
        createSharedPreferencesIfNotExist();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static void saveLong(String key, long value) {
        createSharedPreferencesIfNotExist();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void remove(String key) {
        createSharedPreferencesIfNotExist();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void removeAll() {
        createSharedPreferencesIfNotExist();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public static boolean containsKey(String key) {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.contains(key);
    }

    public static SharedPreferences.Editor getEditor() {
        createSharedPreferencesIfNotExist();
        return sharedPreferences.edit();
    }

    public static SharedPreferences getPreference() {
        createSharedPreferencesIfNotExist();
        return sharedPreferences;
    }

    private static void createSharedPreferencesIfNotExist() {
        if (sharedPreferences == null) {
            sharedPreferences = AppContext.getAppContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
    }
}
