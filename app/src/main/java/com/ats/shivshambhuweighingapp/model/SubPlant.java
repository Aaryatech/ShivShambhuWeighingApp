package com.ats.shivshambhuweighingapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubPlant {
    @SerializedName("subplantId")
    @Expose
    private Integer subplantId;
    @SerializedName("subplantName")
    @Expose
    private String subplantName;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("plantId")
    @Expose
    private Integer plantId;
    @SerializedName("delStatus")
    @Expose
    private Integer delStatus;
    @SerializedName("exInt1")
    @Expose
    private Integer exInt1;
    @SerializedName("exInt2")
    @Expose
    private Integer exInt2;
    @SerializedName("exVar1")
    @Expose
    private String exVar1;
    @SerializedName("exVar2")
    @Expose
    private String exVar2;
    @SerializedName("exDate1")
    @Expose
    private String exDate1;
    @SerializedName("exBool1")
    @Expose
    private Integer exBool1;

    public Integer getSubplantId() {
        return subplantId;
    }

    public void setSubplantId(Integer subplantId) {
        this.subplantId = subplantId;
    }

    public String getSubplantName() {
        return subplantName;
    }

    public void setSubplantName(String subplantName) {
        this.subplantName = subplantName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Integer getExInt1() {
        return exInt1;
    }

    public void setExInt1(Integer exInt1) {
        this.exInt1 = exInt1;
    }

    public Integer getExInt2() {
        return exInt2;
    }

    public void setExInt2(Integer exInt2) {
        this.exInt2 = exInt2;
    }

    public String getExVar1() {
        return exVar1;
    }

    public void setExVar1(String exVar1) {
        this.exVar1 = exVar1;
    }

    public String getExVar2() {
        return exVar2;
    }

    public void setExVar2(String exVar2) {
        this.exVar2 = exVar2;
    }

    public String getExDate1() {
        return exDate1;
    }

    public void setExDate1(String exDate1) {
        this.exDate1 = exDate1;
    }

    public Integer getExBool1() {
        return exBool1;
    }

    public void setExBool1(Integer exBool1) {
        this.exBool1 = exBool1;
    }

    @Override
    public String toString() {
        return "SubPlant{" +
                "subplantId=" + subplantId +
                ", subplantName='" + subplantName + '\'' +
                ", location='" + location + '\'' +
                ", plantId=" + plantId +
                ", delStatus=" + delStatus +
                ", exInt1=" + exInt1 +
                ", exInt2=" + exInt2 +
                ", exVar1='" + exVar1 + '\'' +
                ", exVar2='" + exVar2 + '\'' +
                ", exDate1='" + exDate1 + '\'' +
                ", exBool1=" + exBool1 +
                '}';
    }
}
