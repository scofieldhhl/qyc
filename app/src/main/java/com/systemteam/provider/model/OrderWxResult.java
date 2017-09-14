package com.systemteam.provider.model;

/**
 * 类描述：
 * FAIL
 * {"trade_state": "NOTPAY", "nonce_str": "jT6noaaCpi8AnIw6", "return_code": "SUCCESS",
 * "return_msg": "OK", "sign": "643AE8215C464BAA89ADFBDE6A0510F0", "mch_id": "1488489712",
 * "out_trade_no": "0tBLB830jrUyG0EVSvQLTiFG9xzTSsvE",
 * "trade_state_desc": "\u8ba2\u5355\u672a\u652f\u4ed8",
 * "appid": "wx34a15429655e4678", "result_code": "SUCCESS"}
 *
 * SUCCESS
 * {"openid": "oaxY41fhWTbStB1wH9KViiBlDn1M", "trade_type": "APP",
 * "trade_state": "SUCCESS", "cash_fee": "1", "is_subscribe": "N",
 * "nonce_str": "bBDsRHA0uaPuHVpn", "return_code": "SUCCESS",
 * "return_msg": "OK", "sign": "3F353E5E5D38BA262CCE0421F0F8DED2",
 * "bank_type": "CFT", "attach": null, "mch_id": "1488489712",
 * "out_trade_no": "juRclD9wpDY8XyWTAz8TXBnCfSnUOyol",
 * "transaction_id": "4008372001201709142087471871", "total_fee": "1",
 * "appid": "wx34a15429655e4678", "fee_type": "CNY",
 * "time_end": "20170914194041", "result_code": "SUCCESS"}
 * 创建人：ws
 * 创建时间：2017/9/13 下午8:35
 */

public class OrderWxResult {
    public String trade_state;
    public String nonce_str;
    public String return_code;
    public String return_msg;
    public String sign;
    public String mch_id;
    public String out_trade_no;
    public String trade_state_desc;
    public String appid;
    public String result_code;

    //success
    public String openid;
    public String trade_type;
    public int cash_fee;
    public String is_subscribe;
    public String bank_type;
    public String attach;
    public String transaction_id;
    public int total_fee;
    public String fee_type;
    public String time_end;
}
