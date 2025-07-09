package com.gotogether.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.gotogether.domain.user.repository.UserRepository;
import com.gotogether.global.constants.Constants;
import com.gotogether.global.oauth.handler.CustomAuthenticationEntryPoint;
import com.gotogether.global.oauth.handler.CustomFailureHandler;
import com.gotogether.global.oauth.handler.CustomSuccessHandler;
import com.gotogether.global.oauth.service.CustomOAuth2UserService;
import com.gotogether.global.oauth.service.TokenBlacklistService;
import com.gotogether.global.oauth.util.JWTFilter;
import com.gotogether.global.oauth.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final CustomFailureHandler customFailureHandler;
	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;
	private final TokenBlacklistService tokenBlacklistService;

	@Value("${app.cors.allowed-origins}")
	private String allowedOrigins;

	public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler,
		CustomFailureHandler customFailureHandler,
		JWTUtil jwtUtil, UserRepository userRepository, TokenBlacklistService tokenBlacklistService
	) {

		this.customOAuth2UserService = customOAuth2UserService;
		this.customSuccessHandler = customSuccessHandler;
		this.customFailureHandler = customFailureHandler;
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
		this.tokenBlacklistService = tokenBlacklistService;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {

		http.
			securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**");
		http
			.csrf(csrf -> csrf.disable());
		http
			.httpBasic(httpBasic -> httpBasic.realmName("Swagger API Documentation"));
		http
			.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
		http
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
					CorsConfiguration configuration = new CorsConfiguration();

					List<String> origins = List.of(allowedOrigins.split(","));
					configuration.setAllowedOrigins(origins);
					configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
					configuration.setAllowCredentials(true);
					configuration.setAllowedHeaders(List.of("*"));
					configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(List.of("Set-Cookie", "Content-Disposition"));

					return configuration;
				}
			}));

		http
			.csrf((auth) -> auth.disable());

		http
			.formLogin((auth) -> auth.disable());

		http
			.httpBasic((auth) -> auth.disable());

		http
			.addFilterBefore(new JWTFilter(jwtUtil, userRepository, tokenBlacklistService),
				UsernamePasswordAuthenticationFilter.class);

		http
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(customSuccessHandler)
				.failureHandler(customFailureHandler)
			)

			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
			);

		http
			.authorizeHttpRequests(registry ->
				registry
					.requestMatchers(Constants.NO_NEED_FILTER_URLS.toArray(String[]::new)).permitAll()
					.anyRequest().authenticated()
			);

		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http
			.headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions.disable()));

		return http.build();
	}
}