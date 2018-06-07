package com.zoke.neb.avatar.demo.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zoke.neb.avatar.demo.R;
import com.zoke.neb.avatar.model.Avatar;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by wulijie on 2018/6/7.
 */
public class AvatarAdapter extends BaseQuickAdapter<Avatar, BaseViewHolder> {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AvatarAdapter(@Nullable List<Avatar> data) {
        super(R.layout.item_avatar, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Avatar item) {
        ImageView headIv = helper.getView(R.id.headIv);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setFadeIn(true)
                .setCircular(true)
                .setLoadingDrawableId(R.drawable.ic_f_default)
                .setFailureDrawableId(R.drawable.ic_f_default)
                .build();
        x.image().bind(headIv, item.url, imageOptions);
        DecimalFormat fnum = new DecimalFormat("##0.00");
        float size = Float.parseFloat(item.size) / 1024.0f;
        helper.setText(R.id.tv_title, item.name)
                .setText(R.id.tv_zh, "上传时间：" + formatter.format(new Date(Long.parseLong(item.ts) * 1000)))
                .setText(R.id.tv_size, "头像大小：" + fnum.format(size) + " KB")
                .setText(R.id.tv_address, "" + item.userId);
    }
}
