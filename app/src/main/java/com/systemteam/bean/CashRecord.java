package com.systemteam.bean;

import cn.bmob.v3.BmobObject;

import static com.systemteam.util.Constant.PAY_COUPON_DEFAULT;
import static com.systemteam.util.Constant.PAY_TYPE_WX;

/**
 * 类描述：
 * 创建人：ws
 * 创建时间：2017/8/4 下午5:14
 */

public class CashRecord extends BmobObject {
    private static final long serialVersionUID = 1L;
    private MyUser author;      //用户
    private Integer type = PAY_TYPE_WX;       //0：微信 1：支付宝
    private Float amount;       //金额
    private Float coin;         //充值金额对应账户增加额，默认1:1
    private String phoneType;   //
    private Integer status;     //状态
    private String mark;        //备注
    private Integer coupon = PAY_COUPON_DEFAULT;     //赠送优惠券数，默认0

    public CashRecord(MyUser user, int type, float amount){
        this.author = user;
        this.type = type;
        this.amount = amount;
    }

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getCoin() {
        return coin;
    }

    public void setCoin(Float coin) {
        this.coin = coin;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Integer getCoupon() {
        return coupon;
    }

    public void setCoupon(Integer coupon) {
        this.coupon = coupon;
    }
}
