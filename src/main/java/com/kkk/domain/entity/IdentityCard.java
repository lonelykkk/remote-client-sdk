package com.kkk.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/10/30 19:49
 * @Version V1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentityCard {
    private String birthday;
    private int result; //1.不一致 0.一致
    private String address; //地址
    private String orderNo;  //订单编号
    private String sex; //性别
    private String desc; //描述
}
