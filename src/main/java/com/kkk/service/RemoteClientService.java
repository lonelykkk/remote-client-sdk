package com.kkk.service;

import com.kkk.domain.entity.ImgCaptcha;
import com.kkk.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.kkk.constant.ImgCaptchaConstant.*;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/10/31 9:10
 * @Version V1.0
 */
public class RemoteClientService {
    private static final int CHAR_COUNT = 5; // 验证码字符个数

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

    /**
     * 生成图片验证码api
     * @param count 输入你要生成的验证码的数量
     * @return
     */
    public ImgCaptcha getCaptcha(int count) {
        String captchaText = generateRandomText(count);
        BufferedImage captchaImage = generateCaptchaImage(captchaText);
        ImgCaptcha imgCaptcha = new ImgCaptcha();
        try {
            // 保存生成的验证码图片到 resources/captcha 目录下
            String outputPath = "src/main/resources/captcha";
            File outputDir = new File(outputPath);

            // 创建目录（如果不存在）
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // 设置图片文件路径
            File outputFile = new File(outputDir, captchaText + ".png");
            ImageIO.write(captchaImage, "png", outputFile);

            System.out.println("验证码生成成功！验证码内容: " + captchaText);
            System.out.println("图片路径: " + outputFile.getAbsolutePath());
            imgCaptcha.setCaptchaText(captchaText);
            imgCaptcha.setCaptchaUrl(outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("验证码图片保存失败！");
            e.printStackTrace();
        }
        return imgCaptcha;
    }

    // 随机生成验证码内容
    private String generateRandomText(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_STRING.charAt(random.nextInt(CHAR_STRING.length())));
        }
        return sb.toString();
    }
    // 生成验证码图片
    private BufferedImage generateCaptchaImage(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景颜色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体和颜色
        g.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        g.setColor(Color.BLACK);

        // 画验证码文本
        int x = 10;
        for (int i = 0; i < text.length(); i++) {
            // 随机颜色和旋转角度
            g.setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));
            int degree = new Random().nextInt(30) - 15;
            double theta = Math.toRadians(degree);
            g.rotate(theta, x, HEIGHT - 10);
            g.drawString(String.valueOf(text.charAt(i)), x, HEIGHT - 10);
            g.rotate(-theta, x, HEIGHT - 10);
            x += 20;
        }

        // 添加干扰线
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));
            int x1 = new Random().nextInt(WIDTH);
            int y1 = new Random().nextInt(HEIGHT);
            int x2 = new Random().nextInt(WIDTH);
            int y2 = new Random().nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        g.dispose();
        return image;
    }

    public String getGptKey(String msg) {
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
        return json;

    }

    public String getEmailHtml(String code) {
        String content = "<!DOCTYPE html>\n" +
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
                "        <div class=\"verification-code\">" + code + "</div>\n" +
                "        <p class=\"note\">请注意：验证码将在5分钟后失效，请尽快使用。</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
        return content;
    }

}
