package com.kkk.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
}