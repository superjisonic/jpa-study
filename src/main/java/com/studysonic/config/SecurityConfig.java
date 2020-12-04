package com.studysonic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter; //상속받을시 시큐리티 필터대로 손쉽게 설정 가능


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 내가 지정한 특정 요청들은 시큐리티 인증 체크하지 않도록 설정
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email", "/check-email-tocken",
                        "/email-login", "/check-email-login", "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll()//프로필은 겟요청만 허용해줌
                .anyRequest().authenticated();//이외 다른건 시큐리티 쳌
    }
}
