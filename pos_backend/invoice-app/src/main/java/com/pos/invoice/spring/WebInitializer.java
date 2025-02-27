package com.pos.invoice.spring;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Create the Spring application context
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(SpringConfig.class);
        springContext.setServletContext(servletContext);

        // Create and register the DispatcherServlet
        DispatcherServlet servlet = new DispatcherServlet(springContext);
        ServletRegistration.Dynamic registration = servletContext.addServlet("spring", servlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/*");
    }
} 