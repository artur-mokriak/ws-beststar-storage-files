package com.beststar.storage.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration("swaggerConfigProperties")
public class SwaggerConfigProperties {
    @Value("${swagger.enabled}")
    private String enabled = "false";

    @Value("${swagger.info.version}")
    private String infoVersion;

    @Value("${swagger.info.title}")
    private String infoTitle;

    @Value("${swagger.info.description}")
    private String infoDescription;
}
