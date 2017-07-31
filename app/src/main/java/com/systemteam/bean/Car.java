package com.systemteam.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class Car extends BmobObject {

	private static final long serialVersionUID = 1L;
	private String carNo;		//编号
	private Integer carType;	//类型
	private Float price;		//价格
	private String name;		//车名称
	private BmobDate dateValid;	//绑定激活日期
	private MyUser author;		//商户
	private String address;		//地址
	private BmobGeoPoint position;//GPS定位（经纬度信息）
	private Float income;		//使用总收入
	private Integer status;		//状态 0正常，-1无法开锁，0<-1：故障
	private String password;	//启动密码
	private Float earn;			//商户总收益
	private String mark;		//备注or故障描述

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public Integer getCarType() {
		return carType;
	}

	public void setCarType(Integer carType) {
		this.carType = carType;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BmobDate getDateValid() {
		return dateValid;
	}

	public void setDateValid(BmobDate dateValid) {
		this.dateValid = dateValid;
	}

	public MyUser getAuthor() {
		return author;
	}

	public void setAuthor(MyUser author) {
		this.author = author;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Float getIncome() {
		return income;
	}

	public void setIncome(Float income) {
		this.income = income;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BmobGeoPoint getPosition() {
		return position;
	}

	public void setPosition(BmobGeoPoint position) {
		this.position = position;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Float getEarn() {
		return earn;
	}

	public void setEarn(Float earn) {
		this.earn = earn;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
