package com.systemteam.bean;

import cn.bmob.v3.BmobObject;

public class Feedback extends BmobObject{
	private static final long serialVersionUID = 1L;
	private MyUser author;		//客户
	private String userName;	//姓名
	private String phone;		//手机
	private String address;		//地址
	private String email;		//邮件
	private String content;		//内容
	private Integer status;		//状态0：申请， 1已处理
	private String mark;		//备注

	public Feedback(MyUser user, String address, String email, String content){
		this.author = user;
		this.address = address;
		this.email = email;
		this.content = content;
		if(user != null){
			this.userName = user.getUsername();
			this.phone = user.getMobilePhoneNumber();
		}
	}

	public MyUser getAuthor() {
		return author;
	}

	public void setAuthor(MyUser author) {
		this.author = author;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
