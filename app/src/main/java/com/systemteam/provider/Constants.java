package com.systemteam.provider;

/**
 * Created by chenjiang on 2016/5/16.
 */
public class Constants {

    // 无效参数
    public static final int INVALIDPARAMETER = 3333;

    public static final String REQUESTCODE = "requestcode";
    public static final String SHAERD_FILE_NAME = "shaerd_file_name";
    public static final String KEY_THIRD_LOGIN = "key_third_login";

    public static String URL_EXPERTER_LIST = "http://www.wondershare.json?";

    public static final String HISTORY_IS_LOGIN = "history_is_login";
    public static final String SEARCH_KEYWORD = "keyword";
    public static final String SEARCH_KEYWORD_NAME = "keyword_name";
    public static final int SEARCH_RESULT_LIMIT = 3;
    public static final String ACTION_SEARCH_MORE = "com.ws.dfn.action.search.more";
    public static final String ACTION_REFRESH_AMOUNT = "com.ws.dfn.action.refreshamount";
    public static final String ACTION_REFRESH_FAIL = "com.ws.dfn.action.refresh.fail";
    public static final String MEMBER_ID = "member_id";
    public static final String ORDER_ID = "order_id";
    public static final String USER_TYPE = "user_type";
    public static final String AMOUNT_TXT = "amount_txt";
    public static final String CASHOUT_CURRENCY_AMOUNT = "currency_amount";
    public static final String CASHOUT_CURRENCY_CASH = "currency_cash";
    public static final String CASHOUT_CURRENCY_FEE = "currency_platform_fee";
    public static final String CASHOUT_CURRENCY_UNIT = "unit";
    public static final String WEBRTCWSSERVER="WSServer";
    public static final String WEBRTCGGSERVER="GGServer";

    public static final int LIMIT_PAGE_ORDER = 8;
    public static final float ALPHA_BUTTON = 0.74f;
    public static final int SOCKET_TIMEOUT = 1000;

    public static final int MSG_ADPTER_NOTIF = 0X123;
    public static final int MSG_ERROR = 0X124;
    public static final int MSG_BTN_CLICK = 0X125;
    public static final int MSG_RESPONSE_SUCCESS = 0X126;

    public static final String SHARPRE_SEARCH_RECORD = "search_record";
    public static final String SHARPRE_VERSION_APP = "version_app";

    public static final String LANGUAGE_ZH = "zh";

    public enum OrderType {
        all,
        /*
         * 待支付
         * */
        pending,
        /*
        * 已支付
        * */
        payed,
        /*
        * 已解决
        * */
        solved,
        /*
        * 完成
        * */
        complete,
        /*
        * 结算
        * */
        withdrawal,
        /*
        * 待退款
        * */
        refunding,
        /*
        * 已退款
        * */
        refunded,
        /*
        * 拒绝
        * */
        refuse;

        public static OrderType getEnumFromString(String string) {
            if (string != null) {
                try {
                    return Enum.valueOf(OrderType.class, string.trim().toLowerCase());
                } catch (IllegalArgumentException ex) {
                }
            }
            return null;
        }

    }

    public static final String ORDER_TYPE_ALL = "all";
    public static final String ORDER_TYPE_PENDING = "pending";
    public static final String ORDER_TYPE_PAYED = "payed";
    public static final String ORDER_TYPE_SOLVED = "solved";
    public static final String ORDER_TYPE_COMPLETE = "complete";
    public static final String ORDER_TYPE_WITHDRAWAL = "withdrawal";
    public static final String ORDER_TYPE_REFUNDING = "refunding";
    public static final String ORDER_TYPE_REFUNDED = "refunded";
    public static final String ORDER_TYPE_REFUSE = "refuse";

    public enum CashType {//apply,pending,success,failure
        apply,
        /*
         * 待支付
         * */
        pending,
        /*
        * 已支付
        * */
        success,
        /*
        * 已解决
        * */
        failure;
    }

    public static final String CASH_TYPE_PENDING = "pending";
    public static final String CASH_TYPE_SUCCESS = "success";
    public static final String CASH_TYPE_FAILURE = "failure";

    public enum SkillType{
        phone_setting(1),
        app_use(2),
        samsung_system(3),
        app_fault(4),
        non_samsung(5);

        SkillType(int i) {
            this.index = i;
        }
        private int index;

        public int getIndex() {
            return index;
        }
    }
}
