package com.pos.increff.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {
    "com.pos.increff.dao",
    "com.pos.increff.api",
    "com.pos.increff.flow",
    "com.pos.increff.invoice",
    "com.pos.increff.dto",
    "com.pos.increff.controller",
    "com.pos.increff.service",
    "com.pos.increff.util"
})
@PropertySource("classpath:test.properties")
public class IntegrationTestConfig extends TestConfig {
    // Inherits all the bean definitions from TestConfig
    // but includes controllers for integration testing
} 