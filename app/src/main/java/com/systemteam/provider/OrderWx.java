package com.systemteam.provider;

import com.google.gson.annotations.SerializedName;

/**
 * 类描述：
 * 创建人：ws
 * 创建时间：2017/9/13 下午8:07
 */

public class OrderWx {
    public int code;
    public Data data;

    public class Data{
        @SerializedName("package")
        public String packagevalue;
        public String timeStamp;
        public String sign;
        public String prepayid;
        public String partnerid;
        public String appId;
        public String tradeNo;
        public String nonceStr;
    }
}
