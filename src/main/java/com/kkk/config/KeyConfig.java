package com.kkk.config;

import com.kkk.client.RemoteClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/11/13 8:58
 * @Version V1.0
 */
@Configuration
@ConfigurationProperties("kkk.client")
@ComponentScan
public class KeyConfig {
    private String apiCode;
    private String qq;
    private String qqMailCode;
    private String GPTCode;

    @Bean
    public RemoteClient remoteClient() {
        return new RemoteClient(apiCode, qq, qqMailCode,GPTCode);
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQqMailCode() {
        return qqMailCode;
    }

    public void setQqMailCode(String qqMailCode) {
        this.qqMailCode = qqMailCode;
    }
}
