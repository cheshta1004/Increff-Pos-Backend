package com.increff.pos;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = {
    "com.increff.pos.controller",
    "com.increff.pos.dto",
    "com.increff.pos.service",
    "com.increff.pos.api",
    "com.increff.pos.dao",
    "com.increff.pos.config",
    "com.increff.pos.scheduler"
})
public class PosApplication {
    // Configuration class
} 