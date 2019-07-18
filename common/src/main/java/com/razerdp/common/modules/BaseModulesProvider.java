package com.razerdp.common.modules;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;


/**
 * Created by 大灯泡 on 2019/7/18
 * <p>
 * Description：
 */
public abstract class BaseModulesProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        onInit(getContext());
        return false;
    }

    public abstract void onInit(@NonNull Context context);

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
