package com.encore.thecatch.common.security;

import com.encore.thecatch.common.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig{

    private final JwtAuthFilter jwtAuthFilter;
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf().disable()
                // rest api, jwt 사용해서 csrf 방어 x
            .cors().configurationSource(request -> {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("https://www.catch-crm.shop","http://www.catch-crm.shop", "http://localhost:3000"));
                corsConfiguration.setAllowedMethods(List.of("GET","POST", "PUT","PATCH", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                corsConfiguration.addExposedHeader("New-Access-Token");
                return corsConfiguration;
            });
        httpSecurity.httpBasic(basic -> basic.disable())
            // Http basic Auth  기반으로 로그인 인증창 X
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // jwt 인증 하므로 무연결 상태
            .and()
            .authorizeHttpRequests(req -> req
                .antMatchers("/user/signUp",
                        "/user/doLogin",
                        "/system/admin/signUp",
                        "/admin/doLogin",
                        "/admin/superLogin",
                        "/mailSend",
                        "/admin/mailAuthCheck",
                        "/admin/random/create",
                        "/admin/test",
                        "/tracking_pixel/**",
                        "/user/event/**"
                )
                    .permitAll()
                    // 해당 url은 인증 필요 x
                .anyRequest().authenticated()
                    // 나머지 요청은 인증이 되어야함
            )
            .addFilterBefore(jwtAuthFilter, BasicAuthenticationFilter.class);

        return httpSecurity.build();
    }

    //    스프링 시큐리티를 통해 암호화를 진행
//    시큐리티 설정에서 PasswordEncoder를 구현한 클래스를 빈으로 추가
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
