package com.kkk.service;

import com.kkk.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/10/31 9:10
 * @Version V1.0
 */
public class RemoteClientService {

    /**
     * 发送短信服务
     * @param phone
     * @param content
     * @return
     */
    public Integer sendSms(String phone,String content) {
        String host = "https://zwp.market.alicloudapi.com";
        String path = "/sms/sendv2";
        String method = "GET";
        String appcode = "a8041b5ea4714c448ae5d6f9f7cf54d3";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);

        int number = 100000 + (int)(Math.random() * 900000);

        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("content", "【智能云】您的验证码是" + number + "。如非本人操作，请忽略本短信");


        try {

            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            return number;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


}
