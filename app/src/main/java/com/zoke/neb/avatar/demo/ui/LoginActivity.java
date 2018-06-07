package com.zoke.neb.avatar.demo.ui;

import android.os.Bundle;
import android.view.View;

import com.zoke.neb.avatar.api.NebAvatar;
import com.zoke.neb.avatar.demo.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.demo.dialog.AppLoading;
import com.zoke.neb.avatar.http.SmartCallback;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    private AppLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading = new AppLoading(this, "登录中……");
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .init();
    }

    @Event({R.id.btn_login})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //获取地址

                break;
        }
    }

    @Override
    protected void onResume() {
        //一定要掉这个 否则无法执行个别方法
        NebAvatar.getInstance().onResume(this);
        super.onResume();
    }
}
