package com.systemteam.bean;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {

	private static final long serialVersionUID = 1L;
	private Integer age;
	private Boolean sex;
	private Integer type;	//0用户 1商户
	private String mark;	//
	private Integer balance;//
	private MyUser superUser;//
	private Integer status;	//0申请中 1商户
	private Integer earn;	//收益
	private Integer coupon; //优惠券数

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public MyUser getSuperUser() {
		return superUser;
	}

	public void setSuperUser(MyUser superUser) {
		this.superUser = superUser;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getEarn() {
		return earn;
	}

	public void setEarn(Integer earn) {
		this.earn = earn;
	}

	public Integer getCoupon() {
		return coupon;
	}

	public void setCoupon(Integer coupon) {
		this.coupon = coupon;
	}

	@Override
	public String toString() {
		return getUsername()+"\n"+getObjectId()+"\n"+age+"\n"+balance+"\n"+type+"\n"+status+"\n"+getSessionToken()+"\n"+getEmailVerified();
	}
}
