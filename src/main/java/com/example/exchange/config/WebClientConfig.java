package com.example.exchange.config;

import com.example.exchange.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(Constants.API_FRANKFURTER_DEV_V1)
                .build();
    }
}
