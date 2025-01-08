package com.kkk.client;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkk.constant.CodeConstant;
import com.kkk.domain.entity.HourWeatherList;
import com.kkk.domain.entity.IdentityCard;
import com.kkk.domain.entity.ImgCaptcha;
import com.kkk.domain.enums.PowerChatEnum;
import com.kkk.service.RemoteClientService;
import com.kkk.utils.HttpUtils;
import okhttp3.*;
import org.apache.commons.mail.HtmlEmail;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.kkk.constant.RemoteConstant.*;
import static com.kkk.key.ApiKey.*;

/**
 * 调用第三方接口服务端
 *
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/10/27 11:03
 * @Version V1.0
 */
public class RemoteClient {
    private static final Logger log = LoggerFactory.getLogger(RemoteClient.class);
    private String apiCode;
    private String qq;
    private String qqMailCode;
    private String GPTCode;

    /**
     * 构造器
     *
     * @param apiCode
     * @param qq
     * @param qqMailCode
     * @param GPTCode
     */
    // TODO 封装gpt秘钥
    public RemoteClient(String apiCode, String qq, String qqMailCode, String GPTCode) {
        if (StringUtils.hasText(apiCode)) {
            this.apiCode = ALIYUN_API_CODE;
        } else {
            this.apiCode = apiCode;
        }
        if (!StringUtils.hasText(qq)||!StringUtils.hasText(qqMailCode)) {
            this.qq = SMTP_FROM_QQ;
            this.qqMailCode = MAIL_API_CODE;
        } else {
            this.qq = qq;
            this.qqMailCode = qqMailCode;
        }
        if (GPTCode == null) {
            GPTCode = POWER_AI_CHAT_KEY;
        }
    }

    RestTemplate restTemplate = new RestTemplate();
    RemoteClientService remoteClientService = new RemoteClientService();
    HtmlEmail emails = new HtmlEmail();

    /**
     * 英汉互译
     *
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

    public void ParsingQRCodes(String url) {

    }

    /**
     * 二维码生成接口
     *
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
            log.error("GPT接口调用失败");
            throw new RuntimeException("GPT接口调用失败");
        }
    }

    /**
     * 增强GPT模型，支持gpt-3.5-turbo, gpt-4o-mini, gpt-4。其中gpt-4由于价格过高，每天限制3次调用
     * @param msg 输入你需要问的问题
     * @param version 输入你需要调用的模型，其中 0：gpt-3.5-turbo, 1: gpt-4o-mini 3: gpt-4
     * @return 返回AI回答的问题
     */
    public String getPowerAiChat(String msg,Integer version) {
        return remoteClientService.getPowerAiChat(msg, version,GPTCode);
    }

    /**
     * 增强GPT模型，默认使用 gpt-4o-mini
     * @param msg 输入你需要问的问题
     * @return 返回AI回答的问题
     */
    public String getPowerAiChat(String msg) {
        return remoteClientService.getPowerAiChat(msg, PowerChatEnum.GPT1.getVersion(),GPTCode);
    }

    /**
     * GPT远程调用接口
     *
     * @param msg 出入你的问题
     * @return
     */
    public String getLowerAiChat(String msg) {
        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");

            String json = remoteClientService.getGptKey(msg);

            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(AI_CHAT_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", OPEN_AI_GPT_KEY)
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

            headers.put("Authorization", "APPCODE " + apiCode);
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
     *
     * @param name   输入你的姓名
     * @param idcard 输入你的身份证号
     * @return
     */
    public IdentityCard getAuthenticationCard(String name, String idcard) {
        String path = "/api-mall/api/id_card/check";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();

        headers.put("Authorization", "APPCODE " + apiCode);
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
     *
     * @param phone 输入需要发送到哪个手机号
     * @return
     */
    public Integer sendSms(String phone) {
        return remoteClientService.sendSms(phone, null);
    }


    /**
     * 生成图片验证码api
     *
     * @param count 输入你要生成的验证码的数量
     * @return
     */
    public ImgCaptcha getImgCaptcha(int count) {
        ImgCaptcha captcha = remoteClientService.getCaptcha(count);
        return captcha;
    }

    /**
     * 发送邮箱验证码
     *
     * @param to 输入需要接收验证码的邮箱
     * @return
     */
    public String sendSmtp(String to) {
        String code = CodeConstant.generateRandomText(4);
        try {
            emails.setHostName("smtp.qq.com");
            emails.setCharset("utf-8");
            emails.setSmtpPort(465);
            emails.setSSLOnConnect(true);
            emails.addTo(to);//设置收件人
            emails.setFrom(qq, null);
            emails.setAuthentication(qq, qqMailCode);
            emails.setSubject("验证码来略，快快查收");//设置发送主题
            emails.setMsg(remoteClientService.getEmailHtml(code));//设置发送内容
            emails.send();//进行发送
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 发送邮箱,自定义邮箱内容
     * @param to 接收人
     * @param topic 自定义邮件主题
     * @param content 邮件内容
     * @return
     */
    public String sendSmtp(String to,String topic,String content) {
        String code = CodeConstant.generateRandomText(4);
        try {
            emails.setHostName("smtp.qq.com");
            emails.setCharset("utf-8");
            emails.setSmtpPort(465);
            emails.setSSLOnConnect(true);
            emails.addTo(to);//设置收件人
            emails.setFrom(qq, null);
            emails.setAuthentication(qq, qqMailCode);
            emails.setSubject(topic);//设置发送主题
            emails.setMsg(content);//设置发送内容
            emails.send();//进行发送
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 敏感词过滤接口
     *
     * @param text 传入你需要校验的文本
     * @return 返回结果 1：合规，2：不合规，3：疑似，4：审核失败，-1：api调用失败
     */
    public int SensitiveFilter(String text) {
        String path = "/sensitive_words/filter";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + apiCode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("text", text);

        int flag = -1;
        try {
            HttpResponse response = HttpUtils.doPost(SENSITIVE_FILTER_HOST, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            ObjectMapper mapper = new ObjectMapper();
            String responseString = EntityUtils.toString(response.getEntity());
            JsonNode jsonNode = mapper.readTree(responseString);
            //获取响应结果是否正确
            JsonNode data = jsonNode.get("data");
            JsonNode result = data.get("result");
            String str = result.asText();
            flag = Integer.parseInt(str);
            //1：合规，2：不合规，3：疑似，4：审核失败，-1：api调用失败
            //System.out.println("响应结果为:" + flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    public static void main(String[] args) {

    }
}
