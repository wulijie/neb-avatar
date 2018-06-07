package com.zoke.neb.avatar.http;

/**
 * Created by wulijie on 2018/6/6.
 */
public interface SmartCallback {
    void onStart();

    void onSuccess(String response);

    void onError(String error);
}
