package com.kkk.domain.enums;


public enum ResponseCodeEnum {

    CHAT_GPT_ERROR(900, "gpt调用失败");
    private Integer code;

    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
