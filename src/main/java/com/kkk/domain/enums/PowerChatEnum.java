package com.kkk.domain.enums;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/12/4 9:05
 * @Version V1.0
 */
public enum PowerChatEnum {
    GPT0(0, "gpt-3.5-turbo"),
    GPT1(1, "gpt-4o-mini"),
    GPT2(2, "gpt-4");

    private Integer version;
    private String model;

    PowerChatEnum(Integer version, String model) {
        this.version = version;
        this.model = model;
    }

    public Integer getVersion() {
        return version;
    }

    public String getModel() {
        return model;
    }

    public static PowerChatEnum getByVersion(Integer version) {
        if (version == null) {
            return null;
        }
        for (PowerChatEnum powerChatEnum : PowerChatEnum.values()) {
            if (powerChatEnum.getVersion().equals(version)) {
                return powerChatEnum;
            }
        }
        return GPT0; // 如果没有找到匹配的枚举实例，返回 null
    }
}
