package com.systemteam.provider;

import com.google.gson.annotations.SerializedName;

/**
 * 类描述：
 * invalid total_fee
 * {"msg": "invalid total_fee", "code": "0"}
 *
 * 创建人：ws
 * 创建时间：2017/9/13 下午8:07
 */

public class OrderWx {
    public int code;
    public Data data;

    public class Data{
        @SerializedName("package")
        public String packagevalue;
        public String timestamp;
        public String sign;
        public String prepayid;
        public String partnerid;
        public String appid;
        public String tradeNo;
        public String noncestr;
    }
}
