package com.kkk.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/11/1 19:35
 * @Version V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImgCaptcha {
    private String captchaText;
    private String captchaUrl;
}
