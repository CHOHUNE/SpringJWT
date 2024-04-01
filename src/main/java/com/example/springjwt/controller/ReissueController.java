package com.example.springjwt.controller;


import com.example.springjwt.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    // 해당 컨트롤러는 JWT를 받고, 검증하고, 새로운 JWT를 발급해야 해서 아래 클래스를 주입 받아야 한다.
    private final JWTUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> resissue(HttpServletRequest request, HttpServletResponse response) {


        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired Check
        try{
            jwtUtil.isExpired(refresh);
        }catch(ExpiredJwtException e){

            //response status code
            return new ResponseEntity<>("access token expired", HttpStatus.BAD_REQUEST);
        }

        //refresh 인지 확인 ( 발급시 페이로드에 명시 )
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            //response statuse code

            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 6000000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        //response
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh",newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);


    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }


}
