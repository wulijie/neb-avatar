package com.zoke.neb.avatar.demo;

import android.app.Application;
import android.util.Log;

import com.zoke.neb.avatar.api.NebAvatar;
import com.zoke.neb.avatar.demo.tools.PersistTool;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

/**
 * Created by wulijie on 2018/6/7.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(Conf.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        PersistTool.init(this, getResources().getString(R.string.app_name));
        Recovery.getInstance()
                .debug(Conf.DEBUG)
                .recoverInBackground(false)
                .recoverStack(Conf.DEBUG)
//                .mainPage(MainActivity.class)
                .recoverEnabled(Conf.DEBUG)
                .callback(new MyCrashCallback())
                .silent(false, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .init(this);
        NebAvatar.getInstance().init(this, true);
    }

    static final class MyCrashCallback implements RecoveryCallback {
        @Override
        public void stackTrace(String exceptionMessage) {
            LogUtil.e("exceptionMessage:" + exceptionMessage);
        }

        @Override
        public void cause(String cause) {
            Log.e("zxy", "cause:" + cause);
        }

        @Override
        public void exception(String exceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
            LogUtil.e("exceptionClassName:" + exceptionType);
            LogUtil.e("throwClassName:" + throwClassName);
            LogUtil.e("throwMethodName:" + throwMethodName);
            LogUtil.e("throwLineNumber:" + throwLineNumber);
        }

        @Override
        public void throwable(Throwable throwable) {

        }
    }
}
