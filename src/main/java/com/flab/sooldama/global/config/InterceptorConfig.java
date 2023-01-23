package com.flab.sooldama.global.config;

import com.flab.sooldama.global.auth.AuthInterceptor;
import com.flab.sooldama.global.auth.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.flab.sooldama.global.auth"})
public class InterceptorConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;

	private final LoginInterceptor loginInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
			.addPathPatterns("/products/*", "/users/logout");
		registry.addInterceptor(loginInterceptor)
			.addPathPatterns("/users/login");
	}

}
