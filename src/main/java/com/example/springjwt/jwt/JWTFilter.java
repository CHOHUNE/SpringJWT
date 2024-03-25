package com.example.springjwt.jwt;

import com.example.springjwt.dto.CustomUserDetails;
import com.example.springjwt.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {


    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            filterChain.doFilter(request, response); //다음 필터에 리퀘스트, 리스폰스를 넘겨주는 과정

            return;
        }

        System.out.println("authorization now");
        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1]; // split으로 나눈 후 뒤에 있는 요소를 직접 선택


        // 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) { //직접 구현한 JwtUtil 객체 안 유효기간 만료 메서드로 true 일시 실행되는 메서드
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            //조건이 해당 되면 메소드 종료 ( 필수 )
            return;
        }

        //토큰에서 username 과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userEntity를 생성해 값 set
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppasword");
        userEntity.setRole(role);

        //UserDetail에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
