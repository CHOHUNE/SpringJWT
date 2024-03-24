package com.example.springjwt.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        //csrf disable

        http
                .csrf((auth) -> auth.disable())
                //기존의 Session 방식은 Session이 항상 고정되어 있기 때문에 csrf를 구현이 필수 였는데
                // JWT는 Session을 Stateless 방식으로 하기 때문에 csrf 구현이 필수가 아니게 되었다.

                .formLogin((auth)->auth.disable()) //http formLogin 방식 disable
                .httpBasic((auth) -> auth.disable());  //http basic 인증 방식 disable

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated() //이외의 요청에는 인증이 필요하다 ( 로그인 )
                );

        // 세션 설정하는 부분 : JWT 시 무상태로 설정하는 게 제일 중요하다 .
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));



        return http.build();

    }
}
