package com.zoke.neb.avatar.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.zoke.neb.avatar.demo.Conf;
import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.demo.base.BaseActivity;
import com.zoke.neb.avatar.demo.tools.PersistTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity implements Animation.AnimationListener {

    @ViewInject(R.id.splash)
    RelativeLayout splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.9f, 1.0f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(2500);
        alphaAnimation.setAnimationListener(this);
        splash.startAnimation(alphaAnimation);
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .init();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        String address = PersistTool.getString(Conf.KEY, "");
        if (TextUtils.isEmpty(address)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
