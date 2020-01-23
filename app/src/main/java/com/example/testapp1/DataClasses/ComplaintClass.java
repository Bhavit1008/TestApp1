package com.example.testapp1.DataClasses;

import java.text.SimpleDateFormat;

public class ComplaintClass {
    public String name;
    public String complaint;
    public String status;
    public String timeStamp;
    public String gender="Male";
    public String age;
    public String ilat,ilng;


    public ComplaintClass(String name,String complaint){
        this.name = name;
        this.complaint = complaint;
    }
    public ComplaintClass(String name,String complaint,String status,String timeStamp){
        this.name = name;
        this.complaint = complaint;
        this.status = status;
        this.timeStamp = timeStamp;
    }
    public ComplaintClass(String name,String complaint,String status,String timeStamp,String age,String gender,String ilat,String ilng){
        this.name = name;
        this.complaint = complaint;
        this.status = status;
        this.timeStamp = timeStamp;
        this.age = age;
        this.gender = gender;
        this.ilat = ilat;
        this.ilng = ilng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }
}
