package com.zoke.neb.avatar.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.zoke.neb.avatar.LogUtil;
import com.zoke.neb.avatar.api.NebAvatar;
import com.zoke.neb.avatar.demo.Conf;
import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.demo.adapter.AvatarAdapter;
import com.zoke.neb.avatar.demo.base.BaseActivity;
import com.zoke.neb.avatar.demo.model.AvatarResult;
import com.zoke.neb.avatar.demo.tools.PersistTool;
import com.zoke.neb.avatar.demo.tools.Util;
import com.zoke.neb.avatar.http.SmartCallback;
import com.zoke.neb.avatar.model.Avatar;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_my)
public class MyActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, BaseQuickAdapter.OnItemClickListener {

    @ViewInject(R.id.iv_left)
    TextView iv_left;
    @ViewInject(R.id.iv_right)
    TextView iv_right;
    @ViewInject(R.id.tv_title)
    TextView tv_title;

    private AvatarAdapter adapter;
    private List<Avatar> mList = new ArrayList<>();

    @ViewInject(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @ViewInject(R.id.recycler)
    RecyclerView recycler;

    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv_left.setText("返回");
        iv_right.setVisibility(View.INVISIBLE);
        tv_title.setText("我的头像");
        adapter = new AvatarAdapter(mList);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.baseColor));
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(this, recycler);
        refreshLayout.setRefreshing(true);
        adapter.setOnItemClickListener(this);
        page = 0;
        getAvatarList();
    }

    @Override
    protected void onResume() {
        //一定要掉这个 否则无法执行个别方法
        NebAvatar.getInstance().onResume(this);
        super.onResume();
    }


    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar
                .statusBarView(R.id.status)
                .statusBarDarkFont(false)
                .init();
    }


    @Event(R.id.iv_left)
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
        }
    }


    private void getAvatarList() {
        String address = PersistTool.getString(Conf.KEY, "");
        if (TextUtils.isEmpty(address)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        //获取
        NebAvatar.getInstance().getMyAvatarList(String.valueOf(address), new SmartCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(final String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doResult(response);
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        doError(error);
                    }
                });
            }
        });
    }


    private void doError(String error) {
        if (page != 0) {
            adapter.loadMoreComplete();
        } else {
            refreshLayout.setRefreshing(false);
        }
        Toast.makeText(MyActivity.this, "网络不给力哦:" + error, Toast.LENGTH_LONG).show();
    }

    private void doResult(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject resJ = jsonObject.getJSONObject("result");
            String json = resJ.optString("result");
            AvatarResult result = new Gson().fromJson(json, AvatarResult.class);
            if (result != null && result.data != null) {
                if (page == 0) {
                    mList.clear();
                    refreshLayout.setRefreshing(false);
                }
                mList.addAll(result.data);
                if (mList.size() == result.total) {
                    //没有更多了
                    adapter.loadMoreEnd(false);
                } else {
                    adapter.loadMoreComplete();
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            LogUtil.e(e.toString());
            refreshLayout.setRefreshing(false);
            adapter.loadMoreComplete();
        }
    }


    @Override
    public void onRefresh() {
        page = 0;
        getAvatarList();
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getAvatarList();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Avatar avatar = mList.get(position);
        Util.openBrowser(MyActivity.this, avatar.url);
    }
}
