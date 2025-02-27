package com.pos.increff.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;
import javax.servlet.ServletContext;
import org.springframework.context.annotation.Bean;

@Configuration
public class MockServletContextConfig {
    
    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }
} 
