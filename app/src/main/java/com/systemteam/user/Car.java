package com.systemteam.user;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class Car extends BmobObject {

	private static final long serialVersionUID = 1L;
	private String carNo;
	private Integer carType;
	private Integer price;
	private String name;
	private BmobDate dateValid;
	private MyUser author;
	private String address;
	private Integer income;
	private Integer status;
	private BmobGeoPoint position;
	private String password;
	private Integer earn;

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

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
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

	public Integer getIncome() {
		return income;
	}

	public void setIncome(Integer income) {
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

	public Integer getEarn() {
		return earn;
	}

	public void setEarn(Integer earn) {
		this.earn = earn;
	}
}
