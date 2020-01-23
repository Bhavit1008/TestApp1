package com.example.testapp1.DataClasses;

public class Users {
    public String phoneNumber;
    public String latitude;
    public String longitude;
    public String IMEI;
    public Users(){}

    public Users(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public Users(String phoneNumber,String latitude,String longitude){
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Users(String phoneNumber,String IMEI){
        this.phoneNumber = phoneNumber;
        this.IMEI = IMEI;
    }

    public Users(String phoneNumber,String latitude,String longitude,String IMEI){
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.IMEI = IMEI;
    }
}
