package com.milk.milkweb.config;

import com.milk.milkweb.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;

	private static final String[] AUTHENTICATED_LIST = {
			"/member/mypage/**",
			"/member/find-pwd/**",
			"/board/write",
			"/board/update/**",
			"/board/delete/**",
			"/board/like",
			"/board/uploadImg",
			"/comment/write",
			"/comment/update",
			"/comment/delete/**"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.formLogin(login -> login
				.loginPage("/member/login")
				.defaultSuccessUrl("/")
				.usernameParameter("email")
				.passwordParameter("password")
				.failureHandler((request, response, exception) ->
						response.sendRedirect("/member/login/error"))
		);

		http.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfoEndpointConfig ->
						userInfoEndpointConfig
								.userService(customOAuth2UserService)));

		http.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
				.logoutSuccessHandler((request, response, authentication) ->
						response.sendRedirect("/")));

		http.sessionManagement((sessionManagement) ->
						sessionManagement.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(Arrays.stream(AUTHENTICATED_LIST)
								.map(AntPathRequestMatcher::antMatcher)
								.toArray(AntPathRequestMatcher[]::new))
						.authenticated()
						.requestMatchers(
								new AntPathRequestMatcher("/"),
								new AntPathRequestMatcher("/member/**"),
								new AntPathRequestMatcher("/board/**"),
								new AntPathRequestMatcher("/comment/**")
						).permitAll()
						.anyRequest().authenticated()
				);

		http.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
				.authenticationEntryPoint(
						(request, response, authException) -> {
							response.sendRedirect("/member/login");
						}
				)
		);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	private static final String[] IGNORE_WHITELIST = {
			"/swagger-resources",
			"/swagger-resources/**",
			"/configuration/ui",
			"/configuration/security",
			"/webjars/**",
			"/v3/api-docs/**",
			"/v3/api-docs.yaml",
			"/api/public/**",
			"/api/public/authenticate",
			"/actuator/*",
			"/swagger-ui.html",
			"/swagger-ui/**"
	};

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
				.requestMatchers(
						new AntPathRequestMatcher("/css/**"),
						new AntPathRequestMatcher("/js/**"),
						new AntPathRequestMatcher("/img/**"),
						new AntPathRequestMatcher("favicon.ico"),
						new AntPathRequestMatcher("/error"))

				.requestMatchers(Arrays.stream(IGNORE_WHITELIST)
						.map(AntPathRequestMatcher::antMatcher)
						.toArray(AntPathRequestMatcher[]::new));
	}
}

