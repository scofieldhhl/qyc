package com.systemteam.bean;

public class UseRecord {
    private Long id;
    private String userId;
    private String carNo;
    private String time;
    private String timeUse;
    private float cost = 1.0f;
    private int status = 0;
    private float earnRate;
    private float earn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
