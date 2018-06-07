package com.zoke.neb.avatar.demo.model;

/**
 * Created by wulijie on 2018/6/2.
 * 区块链请求交易状态返回结果
 */
public class Result {
    public int code;
    public String msg;
    public ResultData data;

    public class ResultData {
        public String type;//call
        public String from;
        public String to;
        public String hash;
        public String timestamp;
    }
}
