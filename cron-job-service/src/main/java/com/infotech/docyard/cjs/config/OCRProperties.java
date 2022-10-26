package com.infotech.docyard.cjs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(ignoreUnknownFields = false, prefix = "infotech.tesseractdata")
public class OCRProperties {

    private String path;
}
