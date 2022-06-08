package com.beststar.storage.configurations;

import com.beststar.storage.properties.SwaggerConfigProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig { 
     
    @Bean
    public OpenAPI acustomOpenApi(SwaggerConfigProperties swaggerConfigProperties) {
        return new OpenAPI()
                    .info(new Info()
                    .title(swaggerConfigProperties.getInfoTitle())
                    .version(swaggerConfigProperties.getInfoVersion())
                    .description(swaggerConfigProperties.getInfoDescription())
                    .termsOfService("http://swagger.io/terms/")
                    .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
