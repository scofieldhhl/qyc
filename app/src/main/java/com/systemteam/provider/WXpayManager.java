package com.systemteam.provider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Xml;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;

import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 类描述：
 * 创建人：HHL
 * 创建时间：2017/9/8 21:09
 */

public class WXpayManager {

    private final String KEY_PAY = "f085346874dd4e3b3438734779fbb787";

    private Context mContext;
    String[] arrKey = new String[]{
            "appid"
            ,"attach"
            ,"body"
            ,"mch_id"
            ,"nonce_str"
            ,"notify_url"
            ,"out_trade_no"
            ,"spbill_create_ip"
            ,"total_fee"
            ,"trade_type"
            ,"sign"
    };
//TODO out_trade_no订单号
    String[] arrValue = new String[]{
            Constant.WX_APP_ID
            ,"YoYo"
            ,"yoyoCar Pay"
            ,"1488489712"
            ,""
            ,"http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php"
            ,""
            ,""
            ,"1"
            ,"APP"
            ,""
    };

    public WXpayManager(Context context){
        mContext = context;
        arrValue[4] = getRandomString(32);
        arrValue[6] = getOrderId();
        arrValue[7] = getIPAddress(context);
        StringBuffer stringA = new StringBuffer("");
        for(int i = 0; i < 11; i ++){
            stringA.append(arrKey[i]);
            stringA.append("=");
            stringA.append(arrValue[i]);
            stringA.append("&");
        }
        stringA.append("key=").append(KEY_PAY);
        String stringSignTemp = stringA.toString();
        String sign = MD5(stringSignTemp).toUpperCase();
//        sign=hash_hmac("sha256",stringSignTemp, KEY_PAY).toUpperCase();
        arrValue[10] = sign;

        requestOrder(writeXmlSerial(arrKey, arrValue));
    }

    public void requestOrder(final String params){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,"https://api.mch.weixin.qq.com/pay/unifiedorder",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogTool.d(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogTool.e("Error: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> map = new HashMap<>();
                LogTool.d("params :" + params);
                map.put("Params", params);
                return map;
            }
        };
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(stringRequest);
    }
/**
 * <appid>wx2421b1c4370ec43b</appid>
 <attach>支付测试</attach>
 <body>APP支付测试</body>
 <mch_id>10000100</mch_id>
 <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
 <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
 <out_trade_no>1415659990</out_trade_no>
 <spbill_create_ip>14.23.150.211</spbill_create_ip>
 <total_fee>1</total_fee>
 <trade_type>APP</trade_type>
 <sign>0CB01533B8C1EF103065174F50BCA001</sign>
* */
    public static String writeXmlSerial(String []x,String[]y){
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try{
            serializer.setOutput(writer);
            int i=x.length;
            // <?xml version=”1.0″ encoding=”UTF-8″ standalone=”yes”?>
//            serializer.startDocument("UTF-8",true);
            serializer.startTag(null, "xml");
            for(int j=0;j<i;j++){
                serializer.startTag(null, x[j]);
                serializer.text(y[j]);
                serializer.endTag(null, x[j]);
            }
            serializer.endTag(null, "xml");
            serializer.endDocument();
            return writer.toString();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
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

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public String getOrderId(){
        SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
        String strOrderId = formatter.format(curDate) + getRandomString(16);
        LogTool.d("OrderId : " + strOrderId);
        return strOrderId;
    }

    public static String MD5(String val){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] m = md5.digest();//加密
            return getString(m);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }
}
