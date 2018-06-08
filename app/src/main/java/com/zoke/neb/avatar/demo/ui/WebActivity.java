package com.zoke.neb.avatar.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.demo.base.BaseActivity;
import com.zoke.neb.avatar.demo.fragment.WebFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_web)
public class WebActivity extends BaseActivity {
    @ViewInject(R.id.iv_left)
    TextView iv_left;
    @ViewInject(R.id.iv_right)
    TextView iv_right;
    @ViewInject(R.id.tv_title)
    TextView tv_title;

    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv_left.setText("返回");
        tv_title.setText("");
        iv_right.setVisibility(View.INVISIBLE);
        url = getIntent().getStringExtra("url");
        WebFragment fragment = WebFragment.open(this, R.id.fl_web, url);
        fragment.setWebTitleListener(new WebFragment.WebTitleListener() {
            @Override
            public void onTitle(String title) {
                tv_title.setText(title);
            }
        });
    }

    @Event({R.id.iv_left})
    private void onViewClick(View v) {
        onBackPressed();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar
                .statusBarView(R.id.status)
                .statusBarDarkFont(false)
                .init();
    }

}
