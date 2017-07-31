package com.systemteam.bean;

import cn.bmob.v3.BmobObject;

import static com.systemteam.util.Constant.COST_BASE_DEFAULT;

public class UseRecord extends BmobObject{
    private static final long serialVersionUID = 1L;
    private Long id;
    private String userId;  //用户ID
    private MyUser user;    //用户
    private String carNo;   //车编号
    private String time;    //使用日期
    private String timeUse; //使用时长
    private float cost = COST_BASE_DEFAULT;//费用
    private int status = 0; //状态：0正常，-1退款
    private float earnRate; //分成比例
    private float earn;     //分成

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MyUser getUser() {
        return user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeUse() {
        return timeUse;
    }

    public void setTimeUse(String timeUse) {
        this.timeUse = timeUse;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getEarnRate() {
        return earnRate;
    }

    public void setEarnRate(float earnRate) {
        this.earnRate = earnRate;
    }

    public float getEarn() {
        return earn;
    }

    public void setEarn(float earn) {
        this.earn = earn;
    }
}
