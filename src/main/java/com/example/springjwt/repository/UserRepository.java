package com.example.springjwt.repository;

import com.example.springjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Boolean existsByUsername(String username);

    UserEntity findByUsername(String username);
    // 유저테이블에서 해당 유저를 조회하는 쿼리 : 리턴 타입은 유저 엔티티
    // JPA 작명 관례에 준수해서 findBy 객체명을 적어넣어 주면 알아서 JPADATA가  완성 해준다.
    // 이제 위 메서드를 통해서 특정 유저를 조회할 수 있다.
    // 서비스단에서 구현하면 되는데, CustomUserDetailService 클래스를 만들어서 내부에서 서비스 로직을 구현한다.

}
