package com.systemteam.bean;

import cn.bmob.v3.BmobObject;

public class BankCard extends BmobObject{
	private static final long serialVersionUID = 1L;
	private String cardNumber;	//账号号码
	private String bankName;	//银行名称
	private MyUser author;		//用户
	private String userName;	//姓名
	private String phone;		//手机
	private String mark;		//备注
	private Integer status;		//状态

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

	public MyUser getAuthor() {
		return author;
	}

	public void setAuthor(MyUser author) {
		this.author = author;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
}
