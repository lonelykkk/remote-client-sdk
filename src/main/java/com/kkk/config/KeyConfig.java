package com.kkk.config;

import com.kkk.client.RemoteClient;
import lombok.Data;
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
@Data
@ComponentScan
public class KeyConfig {
    private String apiCode;
    private String qq;
    private String qqMailCode;

    @Bean
    public RemoteClient remoteClient() {
        return new RemoteClient(apiCode, qq, qqMailCode);
    }
}
