package com.kkk.domain.entity;


/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/11/1 19:35
 * @Version V1.0
 */

public class ImgCaptcha {
    private String captchaText;
    private String captchaUrl;

    public ImgCaptcha(String captchaText, String captchaUrl) {
        this.captchaText = captchaText;
        this.captchaUrl = captchaUrl;
    }

    public ImgCaptcha() {
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }

    public String getCaptchaUrl() {
        return captchaUrl;
    }

    public void setCaptchaUrl(String captchaUrl) {
        this.captchaUrl = captchaUrl;
    }
}
