package com.encore.thecatch.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig{

    @Value("${symmetricKey}") // 암호화할 키
    private String symmetricKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/user/doLogin", "/user/signUp","/user/{id}/detail").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

//    스프링 시큐리티를 통해 암호화를 진행
//    시큐리티 설정에서 PasswordEncoder를 구현한 클래스를 빈으로 추가
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AesBytesEncryptor 사용을 위한 Bean등록
    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {

        return new AesBytesEncryptor(symmetricKey,"70726574657374");
    }
}
