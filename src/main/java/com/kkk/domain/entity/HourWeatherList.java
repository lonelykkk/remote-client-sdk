package com.kkk.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HourWeatherList {
    @JsonProperty("remark")
    private String remark;

    @JsonProperty("ret_code")
    private int retCode;
    @JsonProperty("areaid")
    private String areaId;
    @JsonProperty("area")
    private String area;
    @JsonProperty("areaCode")
    private String areaCode;
    private List<HourForecast> hourList;

    public HourWeatherList() {
    }

    public HourWeatherList(String remark, int retCode, String areaId, String area, String areaCode, List<HourForecast> hourList) {
        this.remark = remark;
        this.retCode = retCode;
        this.areaId = areaId;
        this.area = area;
        this.areaCode = areaCode;
        this.hourList = hourList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public List<HourForecast> getHourList() {
        return hourList;
    }

    public void setHourList(List<HourForecast> hourList) {
        this.hourList = hourList;
    }
}