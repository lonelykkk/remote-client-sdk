package com.kkk.client;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkk.constant.CodeConstant;
import com.kkk.domain.entity.HourForecast;
import com.kkk.domain.entity.HourWeatherList;
import com.kkk.domain.entity.IdentityCard;
import com.kkk.domain.entity.ImgCaptcha;
import com.kkk.service.RemoteClientService;
import com.kkk.utils.HttpUtils;
import okhttp3.*;
import org.apache.commons.mail.HtmlEmail;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.kkk.constant.RemoteConstant.*;
import static com.kkk.key.ApiCode.GET_WEATHER_APPCODE;
import static com.kkk.key.ApiCode.IDENTITY_CARD_APPCODE;
import static com.kkk.key.QQSmtpApi.MAIL_API_CODE;
import static com.kkk.key.QQSmtpApi.SMTP_FROM_QQ;

/**
 * 调用第三方接口服务端
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/10/27 11:03
 * @Version V1.0
 */
public class RemoteClient {

    RestTemplate restTemplate = new RestTemplate();
    RemoteClientService remoteClientService = new RemoteClientService();
    HtmlEmail emails=new HtmlEmail();

    /**
     * 英汉互译
     * @param msg 输入你需要翻译的内容
     * @return
     */
    public String getTranslation(String msg) {
        if (!StringUtils.hasText(msg)) {
            throw new RuntimeException("字符串不能为空");
        }
        try {
            String url = TRANSLATION_URL + "?msg=" + msg + "&type=json";
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode textNode = rootNode.path("text");
            String text = textNode.asText();
            return text;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * 二维码生成接口
     * @param msg  输入你要填入二维码的信息
     * @param size 输入二维码的图片大小
     * @return
     */
    public String getImgCode(String msg, Integer size) {
        try {
            String url = IMG_CODE_URL + "?text=" + msg + "&size=" + size + "px";
            ResponseEntity<Resource> responseEntity = restTemplate.getForEntity(url, Resource.class);
            // 获取响应体中的资源
            Resource resource = responseEntity.getBody();
            if (resource != null) {
                // 获取resource目录路径
                Path resourcePath = Paths.get("src", "main", "resources", "imgCode/");
                // 如果imgCode目录不存在，则创建它
                if (!Files.exists(resourcePath)) {
                    Files.createDirectories(resourcePath);
                }
                // 创建文件输出流，用于保存图片
                Path imgPath = resourcePath.resolve(UUID.randomUUID().toString().replace("-", "") + ".png");
                InputStream inputStream = resource.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(imgPath.toFile());

                // 缓冲区，用于存储读取的数据
                byte[] buffer = new byte[1024];
                int bytesRead;

                // 读取数据并写入文件
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // 数据传输完成,保存
                return imgPath.toAbsolutePath().toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * GPT远程调用接口
     *
     * @param msg 出入你的问题
     * @return
     */
    public String getAiChat(String msg) {
        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            String json = "{\n" +
                    "  \"max_tokens\": 1200,\n" +
                    "  \"model\": \"gpt-3.5-turbo\",\n" +
                    "  \"temperature\": 0.8,\n" +
                    "  \"top_p\": 1,\n" +
                    "  \"presence_penalty\": 1,\n" +
                    "  \"messages\": [\n" +
                    "    {\n" +
                    "      \"role\": \"system\",\n" +
                    "      \"content\": \"You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible.\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"content\": " + "\"" + msg + "\"" + "\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(AI_CHAT_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "hk-l0ik8q100004543128fa47fb2113ad7a48be4de270945998")
                    .build();

            Call call = client.newCall(request);
            Response response = call.execute();
            String result = response.body().string();

            JSONObject jsonObject = new JSONObject(result);
            // 获取choices数组中的第一个元素
            JSONObject firstChoice = jsonObject.getJSONArray("choices").getJSONObject(0);

            // 从第一个元素中获取message对象
            JSONObject messageObject = firstChoice.getJSONObject("message");

            // 从message对象中获取content字段
            String content = messageObject.getStr("content");

            // 输出获取到的content内容
            System.out.println(content);
            return content;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * 24小时天气查询接口
     *
     * @param area 输入需要查询的天气地点
     * @return
     */
    public HourWeatherList getWeather(String area) {
        try {

            String path = "/hour24";
            String method = "GET";
            Map<String, String> headers = new HashMap();

            headers.put("Authorization", "APPCODE " + GET_WEATHER_APPCODE);
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("area", area);
            querys.put("areaCode", "areaCode");

            HttpResponse response = HttpUtils.doGet(ALI_WEATHER_HOST, path, method, headers, querys);

            //获取天气列表信息
            String responseString = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseString);
            JsonNode showapiResBody = rootNode.get("showapi_res_body");
            System.out.println(showapiResBody.toString());

            HourWeatherList hourWeatherList = mapper.treeToValue(showapiResBody, HourWeatherList.class);
            return hourWeatherList;
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    /**
     * 身份证实名认证api
     * @param name 输入你的姓名
     * @param idcard 输入你的身份证号
     * @return
     */
    public IdentityCard getAuthenticationCard(String name,String idcard) {
        String path = "/api-mall/api/id_card/check";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();

        headers.put("Authorization", "APPCODE " + IDENTITY_CARD_APPCODE);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("name", name);
        bodys.put("idcard", idcard);


        try {
            HttpResponse response = HttpUtils.doPost(IDENTITY_CARD_HOST, path, method, headers, querys, bodys);
            //获取response的body
            ObjectMapper mapper = new ObjectMapper();
            String responseString = EntityUtils.toString(response.getEntity());
            JsonNode jsonNode = mapper.readTree(responseString);
            //获取响应结果是否正确
            JsonNode result = jsonNode.get("code");
            Integer flag = result.asInt();
            System.out.println("响应结果为:" + flag);


            IdentityCard identityCard = new IdentityCard();
            //如果身份证信息正确，获取响应数据
            if (flag == 200) {
                JsonNode data = jsonNode.get("data");
                identityCard = mapper.treeToValue(data, IdentityCard.class);
            } else {
                identityCard.setResult(1);
                identityCard.setDesc("身份证信息有误");
            }
            System.out.println(responseString);
            return identityCard;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * 发送短信验证
     * @param phone 输入需要发送到哪个手机号
     * @return
     */
    public Integer sendSms(String phone) {
        return remoteClientService.sendSms(phone, null);
    }


    /**
     * 生成图片验证码api
     * @param count 输入你要生成的验证码的数量
     * @return
     */
    public ImgCaptcha getImgCaptcha(int count) {
        ImgCaptcha captcha = remoteClientService.getCaptcha(count);
        return captcha;
    }

    public String sendSmtp(String to) {
        String code = CodeConstant.generateRandomText(4);
        try {
            emails.setHostName("smtp.qq.com");
            emails.setCharset("utf-8");
            emails.setSmtpPort(465);
            emails.setSSLOnConnect(true);
            emails.addTo(to);//设置收件人
            emails.setFrom(SMTP_FROM_QQ,"kkk");
            emails.setAuthentication(SMTP_FROM_QQ,MAIL_API_CODE);
            emails.setSubject("验证码来略，快快查收");//设置发送主题
            emails.setMsg("<!DOCTYPE html>\n" +
                    "<html lang=\"zh-CN\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>QQ邮箱验证码</title>\n" +
                    "    <style>\n" +
                    "        body {\n" +
                    "            font-family: Arial, sans-serif;\n" +
                    "            background-color: #f4f4f4;\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "            height: 100vh;\n" +
                    "        }\n" +
                    "        .container {\n" +
                    "            background-color: #fff;\n" +
                    "            padding: 20px;\n" +
                    "            border-radius: 8px;\n" +
                    "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                    "            text-align: center;\n" +
                    "            max-width: 400px;\n" +
                    "        }\n" +
                    "        .logo {\n" +
                    "            width: 100px;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        h1 {\n" +
                    "            font-size: 24px;\n" +
                    "            color: #333;\n" +
                    "            margin-bottom: 10px;\n" +
                    "        }\n" +
                    "        p {\n" +
                    "            font-size: 16px;\n" +
                    "            color: #666;\n" +
                    "            margin-bottom: 20px;\n" +
                    "            text-align: left;\n" +
                    "        }\n" +
                    "        .verification-code {\n" +
                    "            font-size: 32px;\n" +
                    "            font-weight: bold;\n" +
                    "            color: #007bff;\n" +
                    "            background-color: #e9ecef;\n" +
                    "            padding: 10px 20px;\n" +
                    "            border-radius: 8px;\n" +
                    "            display: inline-block;\n" +
                    "            margin-bottom: 20px;\n" +
                    "        }\n" +
                    "        .note {\n" +
                    "            font-size: 14px;\n" +
                    "            color: #999;\n" +
                    "            text-align: left;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <img src=\"https://mail.qq.com/zh_CN/htmledition/images/logo/qqmail/qqmail_logo_default_200h.png\" alt=\"QQ邮箱\" class=\"logo\">\n" +
                    "        <h1>您的验证码</h1>\n" +
                    "        <p>请使用以下验证码完成您的操作：</p>\n" +
                    "        <div class=\"verification-code\">"+code+"</div>\n" +
                    "        <p class=\"note\">请注意：验证码将在5分钟后失效，请尽快使用。</p>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>");//设置发送内容
            emails.send();//进行发送
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
    public static void main(String[] args) throws Exception{
        RemoteClient remoteClient = new RemoteClient();
        /*try {
            HourWeatherList hourWeatherList = remoteClient.getWeather("南昌");
            // 定义日期格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            for (HourForecast forecast : hourWeatherList.getHourList()) {
                // 解析字符串为 Date 对象
                Date date = dateFormat.parse(forecast.getTime());
                System.out.println("Time: " + date);
                System.out.println("Weather: " + forecast.getWeather());
                System.out.println("Temperature: " + forecast.getTemperature());
                System.out.println("Wind Direction: " + forecast.getWindDirection());
                System.out.println("Wind Power: " + forecast.getWindPower());
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }*/
        /*IdentityCard identityCard = remoteClient.getAuthenticationCard("喻凯", "360427200307130839");
        System.out.println("生日：" + identityCard.getBirthday());
        System.out.println("响应结果(1.不一致 0.一致)：" + identityCard.getResult());
        System.out.println("地址：" + identityCard.getAddress());
        System.out.println("订单编号：" + identityCard.getOrderNo());
        System.out.println("性别：" + identityCard.getSex());
        System.out.println("desc：" + identityCard.getDesc());*/
        /*final Integer sms = remoteClient.sendSms("13970095229");
        System.out.println("验证码为：" + sms);*/
        /*String chat = remoteClient.getAiChat("帮我用java写一个快速排序");
        System.out.println(chat);*/
        /*ImgCaptcha imgCaptcha = remoteClient.getImgCaptcha(5);
        System.out.println("验证码为：" + imgCaptcha.getCaptchaText());
        System.out.println("路径为：" + imgCaptcha.getCaptchaUrl());*/
        String code = remoteClient.sendSmtp("2765314967@qq.com");
        System.out.println("收到的邮箱验证码为：" + code);
    }
}
