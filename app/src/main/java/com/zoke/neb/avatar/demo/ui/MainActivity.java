package com.zoke.neb.avatar.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zoke.neb.avatar.LogUtil;
import com.zoke.neb.avatar.api.NebAvatar;
import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.demo.base.BaseActivity;
import com.zoke.neb.avatar.demo.dialog.AppLoading;
import com.zoke.neb.avatar.demo.model.Result;
import com.zoke.neb.avatar.demo.tools.PersistTool;
import com.zoke.neb.avatar.http.SmartCallback;

import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private AppLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading = new AppLoading(this, "上传中……");
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
                LubanOptions option = new LubanOptions.Builder().setMaxHeight(800).setMaxWidth(800).setMaxSize(102400).create();
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
                                PersistTool.saveString("address", res.data.from);
                                Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_LONG).show();
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
                        }
                    });
                }
            });
        }
    }


}
