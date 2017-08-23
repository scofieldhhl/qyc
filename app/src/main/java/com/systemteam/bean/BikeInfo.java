package com.systemteam.bean;

import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.util.Utils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaolei on 16/12/19.
 */
@Entity
public class BikeInfo implements Serializable {
    @Transient
    private static final long serialVersionUID = -758459502806858414L;
    @Transient
    Car car;
    @Id
    private Long id;
    private double latitude;//精度
    private double longitude;//纬度
    private int imgId;
    private String name;
    private String distance;
    private String time;
    private String carNo;
    private Integer carType;
    private Integer price;
    private String dateValid;
    private String dateUpdate;
    private String authorId;
    private String address;
    private Integer income;
    private Integer status;
    private String password;
    private Integer earn;

    public static List<BikeInfo> infos = new ArrayList<>();


    public BikeInfo(double latitude, double longitude, int imgId, String name, String distance, String time) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.time = time;
    }

    //TODO 计算和当前位置的距离
    public BikeInfo(Car car){
        this.car = car;
        this.latitude = car.getPosition().getLatitude();
        this.longitude = car.getPosition().getLongitude();
        this.imgId = R.mipmap.bike_icon;
        this.name = car.getCarNo();
        if(BikeApplication.mCurrentPosition != null){
            this.distance = Utils.GetDistance(car.getPosition().getLongitude(), car.getPosition().getLatitude(),
                    BikeApplication.mCurrentPosition.getLongitude(), BikeApplication.mCurrentPosition.getLatitude()) + "米";
        }else {
            this.distance = "100米";
        }
        this.time = "10分钟";
    }


    @Generated(hash = 747090367)
    public BikeInfo(Long id, double latitude, double longitude, int imgId,
            String name, String distance, String time, String carNo,
            Integer carType, Integer price, String dateValid, String dateUpdate,
            String authorId, String address, Integer income, Integer status,
            String password, Integer earn) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.time = time;
        this.carNo = carNo;
        this.carType = carType;
        this.price = price;
        this.dateValid = dateValid;
        this.dateUpdate = dateUpdate;
        this.authorId = authorId;
        this.address = address;
        this.income = income;
        this.status = status;
        this.password = password;
        this.earn = earn;
    }


    @Generated(hash = 1479717212)
    public BikeInfo() {
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public double getLatitude() {
        return this.latitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return this.longitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public int getImgId() {
        return this.imgId;
    }


    public void setImgId(int imgId) {
        this.imgId = imgId;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDistance() {
        return this.distance;
    }


    public void setDistance(String distance) {
        this.distance = distance;
    }


    public String getTime() {
        return this.time;
    }


    public void setTime(String time) {
        this.time = time;
    }


    public String getCarNo() {
        return this.carNo;
    }


    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }


    public Integer getCarType() {
        return this.carType;
    }


    public void setCarType(Integer carType) {
        this.carType = carType;
    }


    public Integer getPrice() {
        return this.price;
    }


    public void setPrice(Integer price) {
        this.price = price;
    }


    public String getDateValid() {
        return this.dateValid;
    }


    public void setDateValid(String dateValid) {
        this.dateValid = dateValid;
    }


    public String getDateUpdate() {
        return this.dateUpdate;
    }


    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }


    public String getAuthorId() {
        return this.authorId;
    }


    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }


    public String getAddress() {
        return this.address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public Integer getIncome() {
        return this.income;
    }


    public void setIncome(Integer income) {
        this.income = income;
    }


    public Integer getStatus() {
        return this.status;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getPassword() {
        return this.password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public Integer getEarn() {
        return this.earn;
    }


    public void setEarn(Integer earn) {
        this.earn = earn;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
