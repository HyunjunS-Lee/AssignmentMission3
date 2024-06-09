package com.example.dividendsproject.persist;

import com.example.dividendsproject.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
                                           //우리가 주고받을 entity(domain), 이 entity의 아이디 타입
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    //DividendEntity에 있는 ID와 일치하는 ID를 찾아 일치하는 값을 반환
    List<DividendEntity> findAllByCompanyId(Long companyId);

    @Transactional
    void deleteAllByCompanyId(Long id);

    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
}
