package com.zoo.boardback.global.config.security;

import com.zoo.boardback.global.config.security.filter.JwtAuthenticationFilter;
import com.zoo.boardback.global.config.security.handler.CustomAccessDeniedHandler;
import com.zoo.boardback.global.config.security.handler.CustomAuthenticationEntryPoint;
import com.zoo.boardback.global.config.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
    return
        http
        .authorizeHttpRequests(auth ->
            auth
                .requestMatchers(mvc.pattern("/")).permitAll()
                .requestMatchers(mvc.pattern("/api/v1/auth/**")).permitAll()
                .requestMatchers(mvc.pattern("/api/v1/search/**")).permitAll()
                .requestMatchers(mvc.pattern("/api/v1/comments/board/*")).permitAll()
                .requestMatchers(mvc.pattern("/file/**")).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic().disable()
        .csrf().disable()
        .headers().frameOptions().sameOrigin().and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling() // 권한 문제 발생했을 경우
            .accessDeniedHandler(customAccessDeniedHandler)
            .authenticationEntryPoint(customAuthenticationEntryPoint)
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
            .allowedOrigins("http://localhost:3000");
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
