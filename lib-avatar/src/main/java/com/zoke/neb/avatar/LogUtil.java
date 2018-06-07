package com.zoke.neb.avatar;

import android.util.Log;

/**
 * Created by wulijie on 2018/6/6.
 */
public class LogUtil {
    private LogUtil() {
    }

    public static void e(String msg) {
        if (Conf.DEBUG) {
            Log.e("neb-avartar:", msg);
        }
    }
}
