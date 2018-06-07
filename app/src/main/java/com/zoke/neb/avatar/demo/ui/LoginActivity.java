package com.zoke.neb.avatar.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zoke.neb.avatar.Conf;
import com.zoke.neb.avatar.LogUtil;
import com.zoke.neb.avatar.api.NebAvatar;
import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.demo.base.BaseActivity;
import com.zoke.neb.avatar.demo.dialog.AppLoading;
import com.zoke.neb.avatar.demo.model.Result;
import com.zoke.neb.avatar.demo.tools.PersistTool;
import com.zoke.neb.avatar.http.SmartCallback;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

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
                login();
                break;
        }
    }

    private void login() {
        NebAvatar.getInstance().call(Conf.FUNCTION_GET_AVATAR_LIST, new String[]{"-1", "1"}, new SmartCallback() {
            @Override
            public void onStart() {
                loading.show();
            }

            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("response =" + response);
                        loading.dismiss();
                        Result res = new Gson().fromJson(response, Result.class);
                        if (res != null && res.code == 0 && res.data != null) {
                            PersistTool.saveString(com.zoke.neb.avatar.demo.Conf.KEY, res.data.from);
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intent);
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e("error =" + error);
                        loading.dismiss();
                        Toast.makeText(LoginActivity.this, "登录失败：" + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        //一定要掉这个 否则无法执行个别方法
        NebAvatar.getInstance().onResume(this);
        super.onResume();
    }
}
