package com.zoke.neb.avatar.demo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.zoke.neb.avatar.LogUtil;
import com.zoke.neb.avatar.api.NebAvatar;
import com.zoke.neb.avatar.demo.Conf;
import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.demo.adapter.AvatarAdapter;
import com.zoke.neb.avatar.demo.base.BaseActivity;
import com.zoke.neb.avatar.demo.dialog.AppLoading;
import com.zoke.neb.avatar.demo.model.AvatarResult;
import com.zoke.neb.avatar.demo.model.Result;
import com.zoke.neb.avatar.demo.tools.PersistTool;
import com.zoke.neb.avatar.http.SmartCallback;
import com.zoke.neb.avatar.model.Avatar;

import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private AppLoading loading;

    private AvatarAdapter adapter;
    private List<Avatar> mList = new ArrayList<>();

    @ViewInject(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @ViewInject(R.id.recycler)
    RecyclerView recycler;

    private int page = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    page = 0;
                    refreshLayout.setRefreshing(true);
                    getAvatarList();
                    loading.dismiss();
                    Toast.makeText(MainActivity.this, "交易成功，数据存储成功", Toast.LENGTH_LONG).show();
                    loading.setTips("上传中");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading = new AppLoading(this, "上传中");
        adapter = new AvatarAdapter(mList);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.baseColor));
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(this, recycler);
        refreshLayout.setRefreshing(true);
        page = 0;
        getAvatarList();
    }

    private void getAvatarList() {
        String address = PersistTool.getString(Conf.KEY, "");
        if (TextUtils.isEmpty(address)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        NebAvatar.getInstance().getAvatarList(address, page, 40, new SmartCallback() {
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
        Toast.makeText(MainActivity.this, "网络不给力哦:" + error, Toast.LENGTH_LONG).show();
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
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar
                .statusBarView(R.id.status)
                .statusBarDarkFont(false)
                .init();
    }

    @Override
    protected void onResume() {
        //一定要掉这个 否则无法执行个别方法
        NebAvatar.getInstance().onResume(this);
        super.onResume();
    }

    @Event({R.id.btn_select})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                LubanOptions option = new LubanOptions.Builder()
                        .setMaxHeight(800).setMaxWidth(800).setMaxSize(102400).create();
                CompressConfig config = CompressConfig.ofLuban(option);
                config.enableReserveRaw(true);
                CropOptions.Builder builder = new CropOptions.Builder();
                builder.setWithOwnCrop(true);
                builder.setAspectX(800).setAspectY(800);
                getTakePhoto().onEnableCompress(config, true);
                getTakePhoto().onPickMultipleWithCrop(1, builder.create());
                break;
        }
    }


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        List<TImage> imageList = result.getImages();
        if (imageList != null && imageList.size() != 0) {
            TImage image = imageList.get(0);
            LogUtil.e("getCompressPath =" + image.getCompressPath());
            LogUtil.e("getOriginalPath =" + image.getOriginalPath());
            LogUtil.e("getFromType =" + image.getFromType());

            NebAvatar.getInstance().postImage(image.getCompressPath(), new SmartCallback() {
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
                                PersistTool.saveString(Conf.KEY, res.data.from);
                                Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                loading.setCancelable(true);
                                loading.show("交易pending…，请稍后！");
                                handler.sendEmptyMessageDelayed(1, 15000);
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
                            Toast.makeText(MainActivity.this, "上传失败：" + error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
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
}
