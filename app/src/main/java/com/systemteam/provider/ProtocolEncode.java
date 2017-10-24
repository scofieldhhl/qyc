package com.systemteam.provider;

import com.systemteam.util.LogTool;

import java.util.HashMap;
import java.util.Random;

/**
 * @author rivers
 * @version 1.0
 * @Function 协议传参编码
 * @date ${date}
 */
public class ProtocolEncode {

    public static final String IM_AUTH_GET = "http://api.drfone.wondershare.com/im/auth/get?";
    public static final String IM_AUTH_EXPERT_INFO = "http://api.drfone.wondershare.com/im/auth/expert-info?";

//    public final static String URL_NIU = "http://120.76.77.233:20022/start";
    public final static String URL_NIU = "https://yyc.yiqiniubi.com/api/open/start";
    public final static String URL_NIU_QUERY = "https://yyc.yiqiniubi.com/api/open/query";

    /**
     * 开锁请求
     * 10-11 14:59:49.903 5480-5480/? D/[HHL]com.systemteam.provider.d: a L71--
     * appid=5pGH5pGH6L2m&device_id=21591&nonce_str=revy0ntxts6w7qoskeg58xv4m7h9dwpz--
     10-11 14:59:49.904 5480-5480/? D/[HHL]com.systemteam.provider.d: a L73--
     appid=5pGH5pGH6L2m&device_id=21591&nonce_str=revy0ntxts6w7qoskeg58xv4m7h9dwpz&key=65696e5e3624af4e287bee8559b494d5--
     10-11 14:59:49.907 5480-5480/? D/[HHL]com.systemteam.provider.d: a L87--
     strMD5: 19B1BC392472A8DFE49A1C0F7C486446--
     10-11 14:59:49.907 5480-5480/? I/[HHL]com.systemteam.provider.c: a L45--
     encodeUnlockUrl = http://yyc.yiqiniubi.com:20022/start?appid=5pGH5pGH6L2m&device_id=21591&
     nonce_str=revy0ntxts6w7qoskeg58xv4m7h9dwpz&sign=19B1BC392472A8DFE49A1C0F7C486446--
     10-11 14:59:53.034 5480-5480/? D/[HHL]com.systemteam.BaseActivity$5: a L381--
     {"code":"405","msg":"机器正在使用中!"}--
     * @return
     * @
     */
    public static String encodeUnlockUrl(String deviceId, String out_trade_no) {
        String appId = "5pGH5pGH6L2m";
        String nonce_str = getRandomString(32);

        String key = "65696e5e3624af4e287bee8559b494d5";
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("device_id", deviceId);
        map.put("out_trade_no", out_trade_no);
        map.put("nonce_str", nonce_str);
        String sign = ProtocolUtil.createSign(key, map);

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(URL_NIU)
                .append("?appid=").append(appId)
                .append("&device_id=").append(deviceId)
                .append("&nonce_str=").append(nonce_str)
                .append("&out_trade_no=").append(out_trade_no)
                .append("&sign=").append(sign);
        LogTool.i("encodeUnlockUrl = " + sbUrl.toString());
        return sbUrl.toString();
    }

    /**
     * 开锁请求
     *
     * @return
     * @
     */
    public static String encodeQueryUrl(String deviceId, String out_trade_no) {
        String appId = "5pGH5pGH6L2m";
        String nonce_str = getRandomString(32);
        String key = "65696e5e3624af4e287bee8559b494d5";
        HashMap<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("device_id", deviceId);
        map.put("out_trade_no", out_trade_no);
        map.put("nonce_str", nonce_str);
        String sign = ProtocolUtil.createSign(key, map);

        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(URL_NIU_QUERY)
                .append("?appid=").append(appId)
                .append("&device_id=").append(deviceId)
                .append("&out_trade_no=").append(out_trade_no)
                .append("&nonce_str=").append(nonce_str)
                .append("&sign=").append(sign);
        LogTool.i("encodeQueryUrl = " + sbUrl.toString());
        return sbUrl.toString();
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
