package com.systemteam.provider;

import com.systemteam.util.LogTool;

import java.util.HashMap;

/**
 * @author rivers
 * @version 1.0
 * @Function 协议传参编码
 * @date ${date}
 */
public class ProtocolEncode {

    public static final String IM_AUTH_GET = "http://api.drfone.wondershare.com/im/auth/get?";
    public static final String IM_AUTH_EXPERT_INFO = "http://api.drfone.wondershare.com/im/auth/expert-info?";

    public final static String URL_NIU = "http://120.76.77.233:20022/start";

    /**
     * 32、	B获取维修家/用户订单列表
     *
     * @return
     * @
     */
    public static String encodeUnlockUrl() {
        String appId = "5pGH5pGH6L2m";
        String deviceId = "18789";
        String nonce_str = WXpayManager.getRandomString(32);
//        String nonce_str = "fa79ba697ed34654be734fe1067961f5";

        String key = "65696e5e3624af4e287bee8559b494d5";
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("device_id", deviceId);
        map.put("nonce_str", nonce_str);
        String sign = ProtocolUtil.createSign(key, map);

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(URL_NIU)
                .append("?appid=").append(appId)
                .append("&device_id=").append(deviceId)
                .append("&nonce_str=").append(nonce_str)
                .append("&sign=").append(sign);
        LogTool.i("encodeUnlockUrl = " + sbUrl.toString());
        return sbUrl.toString();
    }


}
