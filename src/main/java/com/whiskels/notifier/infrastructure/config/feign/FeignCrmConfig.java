package com.whiskels.notifier.infrastructure.config.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import java.util.Base64;


public class FeignCrmConfig {
    private static final String USER_ENV_VAR = "API_USER_NAME";
    private static final String PASSWORD_ENV_VAR = "API_USER_PASSWORD";

    @Bean
    public RequestInterceptor requestInterceptor(final Environment environment) {
        String auth = STR."\{environment.getProperty(USER_ENV_VAR)}:\{environment.getProperty(PASSWORD_ENV_VAR)}";
        return requestTemplate -> requestTemplate.header(HttpHeaders.AUTHORIZATION, STR."Basic \{Base64.getEncoder().encodeToString(auth.getBytes())}");
    }

}
