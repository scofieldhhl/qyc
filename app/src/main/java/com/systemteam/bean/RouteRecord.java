package com.systemteam.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

@Entity
public class RouteRecord implements Serializable {
    @Transient
    private static final long serialVersionUID = -758459502806858414L;
    public String cycle_date;
    public String cycle_time;
    public String cycle_distance;
    public String cycle_price;
    public String cycle_points;
    @Id
    private Long id;
    private String userId;
    private String carNo;
    private Date time;
    private String timeUse;
    private float cost = 1.0f;
    private int status = 0;
    private float earnRate;
    private float earn;
    @Generated(hash = 2014346652)
    public RouteRecord(String cycle_date, String cycle_time, String cycle_distance,
            String cycle_price, String cycle_points, Long id, String userId,
            String carNo, Date time, String timeUse, float cost, int status,
            float earnRate, float earn) {
        this.cycle_date = cycle_date;
        this.cycle_time = cycle_time;
        this.cycle_distance = cycle_distance;
        this.cycle_price = cycle_price;
        this.cycle_points = cycle_points;
        this.id = id;
        this.userId = userId;
        this.carNo = carNo;
        this.time = time;
        this.timeUse = timeUse;
        this.cost = cost;
        this.status = status;
        this.earnRate = earnRate;
        this.earn = earn;
    }
    @Generated(hash = 447760962)
    public RouteRecord() {
    }
    public String getCycle_date() {
        return this.cycle_date;
    }
    public void setCycle_date(String cycle_date) {
        this.cycle_date = cycle_date;
    }
    public String getCycle_time() {
        return this.cycle_time;
    }
    public void setCycle_time(String cycle_time) {
        this.cycle_time = cycle_time;
    }
    public String getCycle_distance() {
        return this.cycle_distance;
    }
    public void setCycle_distance(String cycle_distance) {
        this.cycle_distance = cycle_distance;
    }
    public String getCycle_price() {
        return this.cycle_price;
    }
    public void setCycle_price(String cycle_price) {
        this.cycle_price = cycle_price;
    }
    public String getCycle_points() {
        return this.cycle_points;
    }
    public void setCycle_points(String cycle_points) {
        this.cycle_points = cycle_points;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getCarNo() {
        return this.carNo;
    }
    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }
    public Date getTime() {
        return this.time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public String getTimeUse() {
        return this.timeUse;
    }
    public void setTimeUse(String timeUse) {
        this.timeUse = timeUse;
    }
    public float getCost() {
        return this.cost;
    }
    public void setCost(float cost) {
        this.cost = cost;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public float getEarnRate() {
        return this.earnRate;
    }
    public void setEarnRate(float earnRate) {
        this.earnRate = earnRate;
    }
    public float getEarn() {
        return this.earn;
    }
    public void setEarn(float earn) {
        this.earn = earn;
    }

}
