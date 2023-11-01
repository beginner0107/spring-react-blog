package com.zoo.boardback.global.config.security;

import com.zoo.boardback.global.config.security.filter.JwtAuthenticationFilter;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtProvider jwtProvider;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
    return
        http
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers(mvc.pattern("/")).permitAll()
                .requestMatchers(mvc.pattern("/api/v1/auth/**")).permitAll()
                .requestMatchers(mvc.pattern("/api/v1/user/*")).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic().disable()
        .csrf().disable()
        .headers().frameOptions().sameOrigin().and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling() // 권한 문제 발생했을 경우
            .accessDeniedHandler(new AccessDeniedHandler() {
              @Override
              public void handle(HttpServletRequest request, HttpServletResponse response,
                  AccessDeniedException accessDeniedException)
                  throws IOException, ServletException {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setCharacterEncoding("utf-8");
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write("권한이 없는 사용자입니다.");
              }
            })
            .authenticationEntryPoint(new AuthenticationEntryPoint() {
              @Override
              public void commence(HttpServletRequest request, HttpServletResponse response,
                  AuthenticationException authException) throws IOException, ServletException {
                // 인증 문제가 발생했을 경우
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setCharacterEncoding("utf-8");
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write("인증되지 않은 사용자입니다.");
              }
            })
            .and()
        .build();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 URL에 대한 요청을 허용
            .allowedMethods("*") // GET, POST, PUT, PATCH, DELETE 등등 method 허용
            .allowedMethods("*")
            .allowedOrigins("http://localhost:3000"); // localhost:3030에서 오는 요청 허용
      }
    };
  }

  @Bean
  MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
    return new MvcRequestMatcher.Builder(introspector);
  }

  @Bean // Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨.
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
