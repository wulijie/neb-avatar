package com.zoke.neb.avatar.demo.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.zoke.neb.avatar.demo.tools.TitleBar;

import org.devio.takephoto.app.TakePhotoActivity;
import org.xutils.x;

public class BaseActivity extends TakePhotoActivity {

    protected ImmersionBar mImmersionBar;

    protected TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mTitleBar = new TitleBar(this);
        if (isImmersionBarEnabled())
            initImmersionBar();
    }

    protected boolean isImmersionBarEnabled() {
        return true;
    }

    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    @Override
    protected void onDestroy() {
        if (mImmersionBar != null) mImmersionBar.destroy();
        super.onDestroy();
    }
}
