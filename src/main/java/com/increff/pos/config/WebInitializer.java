package com.increff.pos.config;

import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

@PropertySource("classpath:employee-app.properties")
public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	private static final String LOCATION = "/tmp";
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
	private static final long MAX_REQUEST_SIZE = 10 * 1024 * 1024;
	private static final int FILE_SIZE_THRESHOLD = 0;

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { SecurityConfig.class, DbConfig.class, ControllerConfig.class }; // Root configuration
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebConfig.class }; // Servlet configuration
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement(
				LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD
		));
		registration.setInitParameter("publishContext", "true");
		registration.setInitParameter("servlet-name", "primaryDispatcher");
	}
	@Override
	protected String getServletName() {
		return "primaryDispatcher";
	}
}