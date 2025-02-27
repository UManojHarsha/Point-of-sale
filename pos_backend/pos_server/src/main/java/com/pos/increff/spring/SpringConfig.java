//package com.pos.increff.spring;
//
//import org.springframework.context.annotation.*;
//
//@Configuration
//@ComponentScan("com.pos.increff")
//@PropertySources({
//    @PropertySource(value = "file:./employee.properties", ignoreResourceNotFound = true)
//})
//public class SpringConfig {
//    // Application configuration here
//}

package com.pos.increff.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = {
    "com.pos.increff",
    "com.pos.commons"
})
@PropertySource("classpath:employee.properties")
public class SpringConfig implements WebMvcConfigurer {

    //@Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }

// TODO: check and remove if not needed
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}






