package com.systemteam.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author rivers
 * @version 1.0
 * @Function 协议管理类
 * @date ${DATA}
 */
public class ProtocolPreferences {
    private static final String AD_SET_URL_DEBUG = "http://resource.wondershare.com/002/007/config_ad.xml";    //debug
    private static final String DB_PROTOCOL = "db_protocol";
    private static final String PROTOCOL_TIME = "protocol_time";
    private static final String PROTOCOL_REQTIME = "protocol_reqtime";
    public static final String REQTIME_EVERY = "every_time";
    public static final String REQTIME_DAILY = "day_once";
    public static final int INT_DAILY = 24 * 60 * 60 * 1000;    //天
    private static final String PROTOCOL_PROMOTION = "protocol_promotion";
    public static final String PROMOTION_ENABLE = "enable";
    public static final String PROMOTION_PAUSE = "pause";
    public static final String PROTOCOL_PROURL = "protocol_promotion_url";
    private static final String PROTOCOL_DESKTOPGUIDE = "protocol_desktopguide";
    private static final String PROTOCOL_ACCOUNT_INFO = "account_info";
    public static final String PROTOCOL_REGISTER_INFO = "reg_info";
    private static final String PROTOCOL_USER_INFO = "user_info";
    private static final String PROTOCOL_LOGIN_TIMESTAMP = "login_timestamp";

    public static final String NAME_FESTIVAL = "activityShow";
    public static final String NAME_DRAWLUCK = "drawluck";
    public static final String NAME_DESKTOPGUIDE = "desktop_guide";
    public static final String NAME_PROMOTION = "promotion";
    public static final String Expert_PHONE_NUMBER = "PHONE";
    private static final String DB_CASH_DESC = "db_cash_desc";

    public static final String EMAIL_COLLECT = "email_collect";

    private static final String PROTOCOL_TARGET_IM_ID = "target_im_id_chat";


    public static void setUserInfo(Context context, String info) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_USER_INFO, info).apply();
    }

    public static String getUserInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_USER_INFO, "");
    }

    public static void setRegInfo(Context context, String info) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_REGISTER_INFO, info).apply();
    }

    public static String getRegInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_REGISTER_INFO, "");
    }

    public static void setExpert_Phone_Number(Context context, String phoneNumber) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(Expert_PHONE_NUMBER, phoneNumber).apply();
    }

    public static String getExpert_Phone_Number(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(Expert_PHONE_NUMBER, "");
    }


    public static void setPotocolLoginTimestamp(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_LOGIN_TIMESTAMP, time).apply();
    }

    public static String getPotocolLoginTimestamp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_LOGIN_TIMESTAMP, "0");
    }

    public static void setAccountInfo(Context context, String info) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_ACCOUNT_INFO, info).apply();
    }

    public static String getAccountInof(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_ACCOUNT_INFO, "");
    }

    public static void setImId(Context context, String ID) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(IM_ID, ID).apply();
    }

    public static String getImId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(IM_ID, "");
    }

    public static String getImSig(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(IM_SIG, "");
    }

    public static void setImSig(Context context, String imSig) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(IM_SIG, imSig).apply();
    }

    public static void setReqTime(Context context, String time) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_REQTIME, time).apply();
    }

    public static String getReqTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_REQTIME, REQTIME_EVERY);
    }

    public static void setPromotionStatus(Context context, String status) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_PROMOTION, status).apply();
    }

    public static String getPromotionStatus(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_PROMOTION, PROMOTION_PAUSE);
    }

    public static void setDesktopGuideStatus(Context context, String status) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_DESKTOPGUIDE, status).apply();
    }

    public static String getDesktopGuideStatus(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_DESKTOPGUIDE, PROMOTION_PAUSE);
    }

    public static void setPromotionUrl(Context context, String url) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_PROURL, url).apply();
    }

    public static String getPromotionUrl(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_PROURL, "www.wondershare.com");
    }

    /**
     * 记录请求的时间
     *
     * @param time
     */
    public static void setProtocolTime(Context context, long time) {
        SharedPreferences sp = context.getSharedPreferences(
                DB_PROTOCOL, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putLong(PROTOCOL_TIME, time);
        e.apply();
    }

    /**
     * 取得上次请求的时间
     *
     * @return
     */
    public static long getProtocolTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(DB_PROTOCOL,
                Context.MODE_PRIVATE);
        long time = sp.getLong(PROTOCOL_TIME, 0);
        return time;
    }

    public static boolean isCollectedEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getBoolean(EMAIL_COLLECT, false);
    }

    public static void setCollectedEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putBoolean(EMAIL_COLLECT, true).apply();
    }

    public static boolean isConn(Context context) {
        boolean bisConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager != null) {
            NetworkInfo network = conManager.getActiveNetworkInfo();
            if (network != null) {
                bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
            }
        }
        return bisConnFlag;
    }

    //登陆要用到的
    public static final String PROTOCOL_UUID = "protocol_uuid";
    public static final String TOKEN_REQUEST = "token_request";
    public static final String TOKEN_REFRESH = "token_refresh";
    public static final String TOKEN_ACCESS = "token_access";
    public static final String MEMBER_ID = "member_id";

    public static final String IM_ID = "im_id";
    public static final String IM_SIG = "im_sig";

    public static void setProtocolUUID(Context context, String uuid) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_UUID, uuid).apply();
    }

    public static String getProtocolUUID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_UUID, "");
    }

    public static void setTokenRequest(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(TOKEN_REQUEST, token).apply();
    }

    public static String getTokenRequest(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(TOKEN_REQUEST, "");
    }

    public static void setTokenRefresh(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(TOKEN_REFRESH, token).apply();
    }

    public static String getTokenRefresh(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(TOKEN_REFRESH, "");
    }

    public static void setTokenAccess(Context context, String token) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(TOKEN_ACCESS, token).apply();
    }

    public static String getTokenAccess(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(TOKEN_ACCESS, "");
    }

    public static void setMemberId(Context context, String id) {
        if (!id.equals(getMemberId(context))) {
        }
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(MEMBER_ID, id).apply();
        if (protocolPrefCallback != null) {
            protocolPrefCallback.SaveMemberId();
        }
    }

    public static String getMemberId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(MEMBER_ID, "DFN1234567890");
    }

    //清空本地缓存的数据，包括IM_ID,memberid,imsig
    public static void RefreshId(Context context) {
        ProtocolPreferences.setImId(context, "");
        ProtocolPreferences.setImSig(context, "");
        ProtocolPreferences.setMemberId(context, "");
    }

    public static void SetCallback(ProtocolPrefCallback callback1) {
        protocolPrefCallback = callback1;
    }

    static ProtocolPrefCallback protocolPrefCallback;

    public interface ProtocolPrefCallback {
        void SaveMemberId();//通知memberid已经生成
    }

    public static void setCashDesc(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putBoolean(DB_CASH_DESC, true).apply();
    }

    public static boolean getSPCashDesc(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getBoolean(DB_CASH_DESC, false);
    }

    public static void setIMIdChatTo(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        preferences.edit().putString(PROTOCOL_TARGET_IM_ID, id).apply();
    }

    public static String getIMIdChatTo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_PROTOCOL, 0);
        return preferences.getString(PROTOCOL_TARGET_IM_ID, "");
    }
}
