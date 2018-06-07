package com.zoke.neb.avatar.api;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zoke.ipfs.api.IPFS;
import com.zoke.ipfs.http.ProgressCallback;
import com.zoke.neb.avatar.Conf;
import com.zoke.neb.avatar.LogUtil;
import com.zoke.neb.avatar.http.SmartCallback;

import java.util.Arrays;

import io.nebulas.Constants;
import io.nebulas.api.SmartContracts;
import io.nebulas.model.ContractModel;
import io.nebulas.model.GoodsModel;
import io.nebulas.utils.Util;

/**
 * Created by wulijie on 2018/6/6.
 */
public class NebAvatar {

    private NebAvatar() {
    }

    public static final NebAvatar getInstance() {
        return NebAvatar.ClassHolder.INSTANCE;
    }

    //每次请求对应的类似于请求码的一类东西
    private String serialNumber = "";
    private boolean isDoing = false;//判断是否正在执行某个请求的开关
    private SmartCallback callback;//切记 一个页面只能一次请求一个call接口
    private boolean isMainNet;
    private Activity activity;

    /**
     * 初始化
     *
     * @param app
     * @param isMainNet 主网 or 测试网
     */
    public void init(Application app, boolean isMainNet) {
        IPFS.getInstance().init(app);
        this.isMainNet = isMainNet;
    }


    /**
     * 在 activity 的 onResume() 生命周期方法内执行操作
     */
    public void onResume(Activity activity) {
        this.activity = activity;
        if (isDoing) {
            nasQueryTransferStatus();
        }
    }

    /**
     * 获取交易状态-依据 serialNumber
     */
    private void nasQueryTransferStatus() {
        isDoing = false;
        if (TextUtils.isEmpty(serialNumber)) {
            callback.onError("serialNumber is empty");
            return;
        }
        int main_net = isMainNet ? Constants.MAIN_NET : Constants.TEST_NET;
        SmartContracts.queryTransferStatus(main_net, serialNumber, new SmartContracts.StatusCallback() {
            @Override
            public void onSuccess(final String response) {
                LogUtil.e("onSuccess :" + response);
                callback.onSuccess(response);
            }

            @Override
            public void onFail(final String error) {
                LogUtil.e("onFail " + error);
                callback.onError(error);
            }
        });
    }

    /**
     * 上传头像 获得地址
     *
     * @param filePath
     * @param smartCallback
     */
    public void postImage(String filePath, @NonNull SmartCallback smartCallback) {
        this.callback = smartCallback;
        //上传头像
        IPFS.getInstance().postFile(filePath, new ProgressCallback<String>() {
            @Override
            public void onSuccess(String s) {
                post(Conf.FUNCTION_ADD_AVATAR, new String[]{s}, Conf.ADDRESS, Conf.VALUE);
            }

            @Override
            public void onError(String s) {
                callback.onError(s);
            }

            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
                callback.onStart();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
            }
        });
    }


    /**
     * call no call back 需要配合trans查询接口
     *
     * @param functionName 调用合约中的方法名
     * @param args         合约方法需要传的参数
     * @param smartAddress 合约地址
     * @param value        执行合约需要消耗的nas
     */
    private void post(@NonNull String functionName, @NonNull String[] args, @NonNull String smartAddress, @NonNull String value) {
        if (activity == null) {
            callback.onError("activity is null");
            return;
        }
        //初始化请求码
        int main_net = isMainNet ? Constants.MAIN_NET : Constants.TEST_NET;
        serialNumber = Util.getRandomCode(Constants.RANDOM_LENGTH);
        LogUtil.e("do serialNumber =>" + serialNumber);
        isDoing = true;
        GoodsModel goods = new GoodsModel();
        goods.name = smartAddress;
        goods.desc = smartAddress;
        SmartContracts.call(activity,
                main_net, goods,
                functionName, smartAddress,
                value, args,
                serialNumber);
    }

    /**
     * 分页获取头像列表
     *
     * @param fromAddress 发起交易请求的地址 - （当前用户的地址）
     * @param page        从0 开始 如果给-1 则返回所有
     * @param pageSize    每页多少
     * @param callback    回调
     */
    public void getAvatarList(String fromAddress, int page, int pageSize, @NonNull SmartCallback callback) {
        get(Conf.FUNCTION_GET_AVATAR_LIST, new String[]{page + "", pageSize + ""}, fromAddress, callback);
    }

    /**
     * 根据地址 获取某个地址下的头像地址
     *
     * @param fromAddress 发起交易请求的地址 - （当前用户的地址）
     * @param callback    回调
     */
    public void getMyAvatarList(String fromAddress, @NonNull SmartCallback callback) {
        get(Conf.FUNCTION_GET_MY_AVATAR_LIST, new String[]{fromAddress}, fromAddress, callback);
    }


    /**
     * 从星云链获取数据
     *
     * @param functionName  合约中的方法
     * @param args          合约中的方法参数
     * @param userAddress   用户的address
     * @param smartCallback
     */
    public void get(String functionName, Object[] args, String userAddress, @NonNull final SmartCallback smartCallback) {
        if (args == null) args = new Object[]{};
        callback.onStart();
        ContractModel contractModel = new ContractModel();
        contractModel.args = Arrays.toString(args);//notice: Arrays.toString() was need , if not : {"error":"json: cannot unmarshal array into Go value of type string"}
        contractModel.function = functionName;
        SmartContracts.call(contractModel, userAddress, Conf.ADDRESS, 1, new SmartContracts.StatusCallback() {
            @Override
            public void onSuccess(final String response) {
                LogUtil.e("onSuccess :" + response);
                smartCallback.onSuccess(response);
            }

            @Override
            public void onFail(final String error) {
                LogUtil.e("onFail " + error);
                smartCallback.onError(error);
            }
        });
    }


    private static class ClassHolder {
        private static final NebAvatar INSTANCE = new NebAvatar();

        private ClassHolder() {
        }
    }
}
