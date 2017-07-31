package com.systemteam.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
/**
 * 提现bean
 * @Description
 */
public class Withdraw extends BmobObject{
	private static final long serialVersionUID = 1L;
	private BankCard card;		//银行账户
	private String cardNumber;	//账号号码
	private String bankName;	//银行名称
	private String userName;	//姓名
	private String phone;		//手机
	private Float total;		//提现额度
	private BmobDate applyDate;	//提现申请时间
	private Integer status;		//状态0：申请， 10提现成功， 其他
	private String mark;		//备注

	public BankCard getCard() {
		return card;
	}

	public void setCard(BankCard card) {
		this.card = card;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public BmobDate getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(BmobDate applyDate) {
		this.applyDate = applyDate;
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
}
