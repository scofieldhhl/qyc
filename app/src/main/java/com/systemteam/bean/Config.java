package com.systemteam.bean;

import cn.bmob.v3.BmobObject;

public class Config extends BmobObject{
	private static final long serialVersionUID = 1L;
	private String name;		//名称
	private String tag;			//
	private String mark;		//备注
	private Float value;		//
	private Float max;			//
	private Float min;			//

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Float getMax() {
		return max;
	}

	public void setMax(Float max) {
		this.max = max;
	}

	public Float getMin() {
		return min;
	}

	public void setMin(Float min) {
		this.min = min;
	}
}
