package com.example.dividendsproject.persist;

import com.example.dividendsproject.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUsername(String username);//아이디를 기준으로 회원 정보를 찾기 위해 사용

    boolean existsByUsername(String username);//회원가입을 할 때 이미 존재하는 아이디인지 확인
}
