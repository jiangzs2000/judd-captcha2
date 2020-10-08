package com.shuyuan.judd.captcha.config;


import com.shuyuan.judd.captcha.config.intercepters.GlobalAspectInteceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@EnableSwagger2
@RefreshScope
public class GlobalBeanConfig {

    @Value("${server.port}")
    private int serverPort;

    @Bean(name = "loggerInteceptor")
    public GlobalAspectInteceptor getLoggerInteceptor() {
        return new GlobalAspectInteceptor();
    }

    @Bean
    public Docket createRestApi() throws UnknownHostException {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .host(InetAddress.getLocalHost().getHostAddress() + ":" + serverPort)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.shuyuan.judd.captcha"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("spring cloud application, https://github.com/chxfantasy")
                .description("https://github.com/chxfantasy")
                .termsOfServiceUrl("https://github.com/chxfantasy")
                .version("1.0")
                .build();
    }
}