package com.increff.pos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PosApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 