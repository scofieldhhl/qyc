package com.systemteam.util;

public class Constant {
    //默认值
    public static final float EARN_RATE_DEFAULT = 0.3f;
    public static final float COST_BASE_DEFAULT = 1.0f;
    public static final long TIME_ONCE_ACTIVE = 3 * 60 * 1000;
    public static int COUPON_DEFAULT = 3;
    public static int CYCLE_DAY_CHART = 15;

    public static final int STATUS_NORMAL = 0;          //正常
    public static final int BREAK_STATUS_LOCK = -1;     //无法开锁

    public static final int STATUS_EXPERT_NORMAL = 0;   //设备正常
    public static final int STATUS_EXPERT_WAITING = -1; //设备维修中
    //guide
    public static final int BREAK_TYPE_LOCK = -1;
    public static final int BREAK_TYPE_BREAK = -2;
    public static final int GUIDE_TYPE_PAY = -3;
    public static final int GUIDE_TYPE_PROCOTOL = -4;
    //guide

    public static final int QUERY_LIMIT_DEFAULT = 10;
    public static int WITHDRAW_DAYS_DEFAULT = 15;
    public static float WITHDRAW_AMOUNT_DEFAULT = 300f;
    public static final int WITHDRAW_SUCCESS = 10;
    public static final int WITHDRAW_FAIL = -1;

    public static final int USER_TYPE_NORMAL = 0;
    public static final int USER_TYPE_CUSTOMER = 1;
    public static final int USER_TYPE_APPLYING = -1;    //申请成为商户
    public static final int USER_TYPE_EXPERTER = 2;     //维护维修人员
    //map
    public static final int MAP_SCAN_SPAN = 10 * 1000;  //设置地图扫描间隔，单位是毫秒
    public static final float MAP_SCALING = 18.0f;      ////地图缩放比设置为18
    //map
    public static final String REQUEST_KEY_BY_USER = "author";
    public static final String REQUEST_KEY_BY_CAR = "car";
    public static final String REQUEST_KEY_BY_CARNO = "carNo";
    public static final String TIME_ONCE_ACTIVE_STR = "03 : 00";
    public static final String BUNDLE_CAR = "key_car";
    public static final String BUNDLE_CARNO = "key_carno";
    public static final String BUNDLE_USER = "key_user";
    public static final String BUNDLE_TYPE_MENU = "key_type";
    public static final String BUNDLE_KEY_CODE = "key_code";
    public static final String BUNDLE_KEY_UNLOCK = "key_unlock";
    public static final String BUNDLE_KEY_ALL_EARN = "key_all_earn";
    public static final String BUNDLE_KEY_ALL_WITHDRAW = "key_all_withdraw";
    public static final String BUNDLE_KEY_ALL_COST = "key_all_cost";
    public static final String BUNDLE_KEY_BLANACE = "key_balance";
    public static final String BUNDLE_KEY_AMOUNT = "key_amount";
    public static final String BUNDLE_KEY_ISGAMEOVER = "key_isgameover";
    public static final String BUNDLE_KEY_SUBMIT_SUCCESS = "key_issubmitsuccess";
    public static final String BUNDLE_KEY_IS_ACTIVING = "key_isactivting";
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_CODE = 101;
    public static final int REQUEST_CODE_WALLET = 102;
    public static final int REQUEST_CODE_BREAK = 103;

    public static final int DISMISS_SPLASH = 0x122;
    public static final int MSG_RESPONSE_SUCCESS = 0x123;
    public static final int MSG_UPDATE_UI = 0x124;
    public static final int MSG_LOGOOUT = 0x125;
    public static final int MSG_WITHDRAW_SUCCESS = 0x126;


    public static final String SHAERD_FILE_NAME = "shared_file_name";
    public static final String ACTION_BROADCAST_ACTIVE = "com.locationreceiver";

    public static final String FORMAT_TIME = "0%s : %s";

    //pay
    public static final String WX_APP_ID = "wx34a15429655e4678";
    public static final int PAY_TYPE_WX = 0;
    public static final int PAY_TYPE_ALI = 1;

    public static final String ALI_APP_ID = "2017082708417182";
    public static final int PHONE_TYPE_IPHONE = 0;
    public static final int PHONE_TYPE_ANDROID = 1;
    public static final int PAY_COUPON_DEFAULT = 0;
    public static final float PAY_AMOUNT_DEFAULT = 5f;
    //pay

    public static final int NETWORK_STATUS_NO = 0;
    public static final int NETWORK_STATUS_GRPS = 1;
    public static final int NETWORK_STATUS_WIFI = 2;
}
