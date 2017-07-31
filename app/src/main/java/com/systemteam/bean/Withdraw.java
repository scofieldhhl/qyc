package com.systemteam.bean;

import cn.bmob.v3.BmobObject;
/**
 * 提现bean
 * @Description
 */
public class Withdraw extends BmobObject{
	private static final long serialVersionUID = 1L;
	private MyUser author;		//客户
	private BankCard bankCard;		//银行账户
	private String cardNumber;	//账号号码
	private String bankName;	//银行名称
	private String userName;	//姓名
	private String phone;		//手机
	private Float amout;		//提现额度
	private Integer status;		//状态0：申请， 10提现成功， 其他
	private String mark;		//备注

	public Withdraw(MyUser user, BankCard card, Float amout){
		this.author = user;
		this.bankCard = card;
		this.amout = amout;
		if(card != null){
			this.cardNumber = card.getCardNumber();
			this.bankName = card.getBankName();
			this.userName = card.getUserName();
			this.phone = card.getPhone();
		}
	}

	public MyUser getAuthor() {
		return author;
	}

	public void setAuthor(MyUser author) {
		this.author = author;
	}

	public BankCard getBankCard() {
		return bankCard;
	}

	public void setBankCard(BankCard bankCard) {
		this.bankCard = bankCard;
	}

	public Float getAmout() {
		return amout;
	}

	public void setAmout(Float amout) {
		this.amout = amout;
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
