package com.example.dividendsproject.persist;

import com.example.dividendsproject.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
                                           //우리가 주고받을 entity(domain), 이 entity의 아이디 타입
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    //메소드 시그니처만 존재함-> 이것을 사용할 수 있는 이유는 스프링부트에서 Repository에 정해준 규칙이 있음
    //규칙에 맞는 네이밍으로 시그니처 함수를 정의하면 스프링부트에서 자동적으로 메소드 내부에 코드를 생성해 실행해줌
    boolean existsByTicker(String ticker); //존재 여부를 boolean 값으로 받음

    //회사 정보 조회
    //CompanyEntity로 바로 받지 않고 Optional로 감싼 이유는 null point exception 방지, 값이 없는 경우도
    //코드 적으로 깔끔하게 처리할 수 있는 장점
    Optional<CompanyEntity> findByName(String name);

    Optional<CompanyEntity> findByTicker(String ticker);

    //LIKE연산자 사용, 특정 키워드로 시작하는 회사명 찾기, 대소문자 무시
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
