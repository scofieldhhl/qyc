package com.systemteam.provider;

import android.content.Context;

import com.google.gson.Gson;
import com.systemteam.util.LogTool;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author rivers
 * @version 1.0
 * @Function 协议方法
 * @date ${date}
 */
public class ProtocolUtil {

    private Context mContext;

    private static ProtocolUtil mInstance;

    public ProtocolUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static ProtocolUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ProtocolUtil(context);
        }
        return mInstance;
    }




    /***
     * 根据参数创建vc值
     *
     * @param appKey
     * @param data
     * @return vc_url
     */
    public static String createSign(String appKey, Map<String, String> data) {
        List<String> l = new ArrayList<>();
//        l.addAll(data.keySet());
//        Collections.sort(l);
        l.add("appid");     //必须是按照文档上的这种次序
        l.add("device_id");
        l.add("out_trade_no");
        l.add("nonce_str");
        StringBuffer sb = new StringBuffer();
        for (String k : l) {
            if (data.get(k) != null) {
                sb.append(quoteStr(k)).append("=").append(quoteStr(URLDecoder.decode(data.get(k))))
                .append("&");
            }
        }
        sb.deleteCharAt(sb.lastIndexOf("&"));
        String jsonStr = sb.toString().replace("\"", "");
        LogTool.d(jsonStr);
        String content = String.format("%s&key=%s", jsonStr, appKey);
        LogTool.d(content);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (null == md)
            return "";
        else
            try {
//                String strMD5 = toHex(md.digest(content.getBytes("UTF-8"))).toUpperCase();
                String strMD5 = MD5.getMessageDigest(content.getBytes("UTF-8")).toUpperCase();
                LogTool.d("strMD5: " + strMD5);
//                String strMD5 = MD5Sign.md5(content).toUpperCase();
//                return HMACSHA256(strMD5.getBytes("UTF-8"), "HMACSHA256".getBytes("UTF-8")).toUpperCase();
//                return "6AC1FFE0CAE7B2041282A6F8DF8279CC";
                return strMD5;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "";
    }


    public static String HMACSHA256(byte[] data, byte[] key)
    {
        try  {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            return byte2hex(mac.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String byte2hex(byte[] b)
    {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }


    /***
     * 根据参数创建vc值
     *
     * @param appKey
     * @param fp
     * @return vc_url
     */
    public static String createVCByJson(String appKey, String fp, Map dataJson) {
        String jsonStr = new Gson().toJson(dataJson);
        System.out.println(jsonStr);
        String content = String.format("%s%s%s", appKey, jsonStr, fp);
        System.out.println(content);

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (null == md)
            return "";
        else
            try {
                return toHex(md.digest(content.getBytes("UTF-8"))).toLowerCase();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        return "";
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    private static String quoteStr(String s) {
        return "\"" + s + "\"";
    }

    public boolean isCorrectEmailFormat(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
