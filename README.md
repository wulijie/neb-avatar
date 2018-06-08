# neb-avatar 使用说明

`neb-avatar` 旨在为 Android 人员 开发无法存储头像的问题。在使用过程中如遇到问题，可联系 `androidzk@163.com` 或者在[ neb-avatar](https://github.com/wulijie/neb-avatar) 的 `Issues`  提问。

- 信息记录基于`星云链`


- 文件存储基于`IPFS`。

### 如何接入

```groovy
//添加依赖
allprojects {
    repositories {
        google()
        jcenter()
		//jitpack need
        maven { url 'https://jitpack.io' }
    }
}

//去 realse 版本那查看最新版本
implementation 'com.github.wulijie:neb-avatar:1.0'

```

### 使用说明

```java
//在应用程序入口调用 init 初始化
public class App extends Application {
	// 第二个参数 主网还是测试网
    NebAvatar.getInstance().init(this, true);
}
```

在`Activity` 的 `onResume()`生命周期方法内调用

```java
@Override
    protected void onResume() {
        //一定要掉这个 否则无法执行个别方法
        NebAvatar.getInstance().onResume(this);
        super.onResume();
    }
```

### API 说明

**上传头像**

```java
	/**
     * 上传头像 获得地址
     * @param filePath
     * @param smartCallback
     */
   postImage(String filePath, @NonNull SmartCallback smartCallback)
```

**获取所有头像数据**

```java
/**
     * 分页获取头像列表
     *
     * @param fromAddress 发起交易请求的地址 - （当前用户的地址）
     * @param page        从0 开始 如果给-1 则返回所有
     * @param pageSize    每页多少
     * @param callback    回调
     */
   getAvatarList(fromAddress,page,pageSize,SmartCallback);
```

**获取某个地址下的所有头像数据**

数据存储结构按照时间的倒叙排列的，新上传的会在最顶部。

这样为某一个用户提供头像接口时，添加完成以后，可直接通过该接口获取列表后，获取当前第一条数据就是最新添加的接口，下面的数据则为该用户的添加历史。

```java
/**
     * 根据地址 获取某个地址下的头像地址
     *
     * @param fromAddress 发起交易请求的地址 - （当前用户的地址）
     * @param callback    回调
     */
   getMyAvatarList(String fromAddress, @NonNull SmartCallback callback)
```

**星云头像已开源 [neb-avatar](https://github.com/wulijie/neb-avatar)**

效果：

![](https://ws3.sinaimg.cn/large/006tNc79ly1fs3mg3m570j30ku13d0w9.jpg)