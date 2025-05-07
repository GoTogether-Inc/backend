package com.gotogether.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gotogether.global.constants.Constants;
import com.gotogether.global.interceptor.pre.AuthUserArgumentResolver;
import com.gotogether.global.interceptor.pre.JWTInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMVCConfig implements WebMvcConfigurer {

	private final AuthUserArgumentResolver authUserArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(authUserArgumentResolver);
	}

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new JWTInterceptor())
			.addPathPatterns("/api/v1/**")
			.excludePathPatterns(Constants.NO_NEED_FILTER_URLS);
	}
}