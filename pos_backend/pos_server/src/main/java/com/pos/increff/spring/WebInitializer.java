package com.pos.increff.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * This class is a hook for <b>Servlet 3.0</b> specification, to initialize
 * Spring configuration without any <code>web.xml</code> configuration. Note
 * that {@link #getServletConfigClasses} method returns {@link SpringConfig},
 * which is the starting point for Spring configuration <br>
 * <b>Note:</b> You can also implement the {@link WebApplicationInitializer }
 * interface for more control
 */

@Configuration
@ComponentScan(basePackages = {"com.pos.increff"})
public class WebInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) {
		// Create the root context
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(SpringConfig.class);
		rootContext.scan("com.pos.increff");
		
		// Register the root context as a servlet context parameter
		container.addListener(new ContextLoaderListener(rootContext));
		
		// Create the dispatcher servlet's context
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.setParent(rootContext);
		
		// Create and register the dispatcher servlet
		DispatcherServlet dispatcherServlet = new DispatcherServlet(dispatcherContext);
		dispatcherServlet.setPublishEvents(false);
		
		ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", dispatcherServlet);
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		dispatcher.setAsyncSupported(true);
	}
}