package com.kkk.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;


public class HourForecast {
    @JsonProperty("time")
    private String time;

    @JsonProperty("wind_direction")
    private String windDirection;

    @JsonProperty("wind_power")
    private String windPower;

    @JsonProperty("areaid")
    private String areaId;

    @JsonProperty("weather_code")
    private String weatherCode;

    @JsonProperty("temperature")
    private String temperature;

    @JsonProperty("area")
    private String area;

    @JsonProperty("weather")
    private String weather;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public HourForecast(String time, String windDirection, String windPower, String areaId, String weatherCode, String temperature, String area, String weather) {
        this.time = time;
        this.windDirection = windDirection;
        this.windPower = windPower;
        this.areaId = areaId;
        this.weatherCode = weatherCode;
        this.temperature = temperature;
        this.area = area;
        this.weather = weather;
    }

    public HourForecast() {
    }
}