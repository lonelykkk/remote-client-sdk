package com.kkk.constant;

import java.util.Random;

import static com.kkk.constant.ImgCaptchaConstant.CHAR_STRING;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/11/12 9:26
 * @Version V1.0
 */
public class CodeConstant {
    public static String generateRandomText(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_STRING.charAt(random.nextInt(CHAR_STRING.length())));
        }
        return sb.toString();
    }
}
