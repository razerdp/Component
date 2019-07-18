package com.razerdp.baselib.interfaces;


import androidx.annotation.Keep;

/**
 * Created by 大灯泡 on 2019/7/18.
 */
@Keep
public interface SimpleCallback<T> {
    void onCall(T data);
}
