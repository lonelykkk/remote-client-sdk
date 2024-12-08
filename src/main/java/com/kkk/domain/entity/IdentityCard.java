package com.kkk.domain.entity;


/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/10/30 19:49
 * @Version V1.0
 */
public class IdentityCard {
    private String birthday;
    private int result; //1.不一致 0.一致
    private String address; //地址
    private String orderNo;  //订单编号
    private String sex; //性别
    private String desc; //描述

    public IdentityCard() {
    }

    public IdentityCard(String birthday, int result, String address, String orderNo, String sex, String desc) {
        this.birthday = birthday;
        this.result = result;
        this.address = address;
        this.orderNo = orderNo;
        this.sex = sex;
        this.desc = desc;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
