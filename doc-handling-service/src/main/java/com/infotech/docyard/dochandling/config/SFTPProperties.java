
package com.infotech.docyard.dochandling.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "sftp.client")
public class SFTPProperties {

    private String host;
    private Integer port;
    private String protocol;
    private String username;
    private String password;
    private String root;
    private String privateKey;
    private String passphrase;
    private String sessionStrictHostKeyChecking;
    private Integer sessionConnectTimeout;
    private Integer channelConnectedTimeout;

}
