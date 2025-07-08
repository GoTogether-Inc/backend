package com.gotogether.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gotogether.global.constants.Constants;
import com.gotogether.global.interceptor.LoggingInterceptor;
import com.gotogether.global.interceptor.pre.AuthUserArgumentResolver;
import com.gotogether.global.interceptor.pre.JWTInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMVCConfig implements WebMvcConfigurer {

	private final AuthUserArgumentResolver authUserArgumentResolver;
	private final LoggingInterceptor loggingInterceptor;


	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(authUserArgumentResolver);
	}

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new JWTInterceptor())
			.addPathPatterns("/api/v1/**")
			.excludePathPatterns(Constants.NO_NEED_FILTER_URLS);

		// TODO: OAuth 로깅 확인하기
		registry.addInterceptor(loggingInterceptor)
            .addPathPatterns(
				"/oauth/authorization/**",
				"/api/v1/users/**",
				"/api/v1/hostchannels/**",
				"/api/v1/events/**",
				"/api/v1/hashtags/**",
				"/api/v1/reference-links/**",
				"/api/v1/orders/**",
				"/api/v1/bookmarks/**",
                "/api/v1/tickets/**",
				"/api/v1/ticketQrCodes/**",
				"/api/v1/ticket-options/**",
				"/api/v1/ticket-option-assignments/**",
				"/api/v1/ticket-answers/**",
                "/api/v1/reservationEmails/**"
            );
	}
}