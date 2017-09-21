package com.systemteam.bean;

/**
 * SUCCESS
 * {"code": "1", "data": {
 * "tradeNo": "A2017092119330389953", "aliPayStr":
 * "app_id=2017082708417182&biz_content=%7B%22out_trade_no%22%3A%22A2017092119330389953%22%2C%22
 * product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22total_amount%22%3A%221%22%2C%22
 * subject%22%3A%22%5Cu6447%5Cu661f%5Cu7403-%5Cu7528%5Cu6237%5Cu5145%5Cu503c%22%7D&
 * charset=utf-8&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F1.rockingcar.applinzi.com%2Fnotify&
 * sign_type=RSA&timestamp=2017-09-21+19%3A33%3A03&version=1.0&
 * sign=gbRizbPBK7jatePSFQE87NmHx4eQv%2F%2BtZqh5LTA2bWRBKrgqEr9EavdWENeP43VYOXVZAHd3SvPSnBe5RPQ5w%2
 * BlhI0IkKSjXt%2BAsrEvWlJpw5hwpcq1u5wx20M5XVYqpLRTPhBIyf%2BXxG2dlpzluuc%2FhwCHoiTVip6pyBOBr8UM%3D"}}
 * 创建人：ws
 * 创建时间：2017/9/13 下午8:07
 */

public class OrderAli {
    public int code;
    public Data data;

    public class Data{
        public String tradeNo;
        public String aliPayStr;
    }
}
