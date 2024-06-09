package com.example.dividendsproject.web;

import com.example.dividendsproject.model.Company;
import com.example.dividendsproject.model.constants.CacheKey;
import com.example.dividendsproject.persist.entity.CompanyEntity;
import com.example.dividendsproject.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company") //경로에 공통되는 부분은 여기로 뺄 수 있음 /company/autocomplete-> /autocomplete
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private final CacheManager redisCacheManager;

    //회사 검색 때 자동 완성
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autoComplete(@RequestParam String keyword) {
        //실제로 저장된 trie에서 데이터를 가져오는 로직
        //var result = this.companyService.autocomplete(keyword);

        var result = this.companyService.getCompanyNamesByKeyword(keyword);//LIKE연산자 사용
        return ResponseEntity.ok(result);
    }//trie 는 서버에서 db부하 낮음, like은 db에서 함으로 db부하 가능성

    //회사 리스트 조회
    @GetMapping //    @GetMapping("/company") -> company는 공통으로 빼주었으니 삭제
    @PreAuthorize("hasRole('READ')")//읽기 권한이 있는 유저만 addCompany API를 호출할 수 있게 제약을 검
                                           //클라이언트에서 페이징관련 옵션을 추가해서 api를 호출할 수 있게 됨
    public ResponseEntity<?> searchCompany(final Pageable pageable) {//pageable 값이 바뀌는 걸 방지하기 위해 fianl
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    //회사 데이터 저장
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")//쓰기 권한이 있는 유저만 addCompany API를 호출할 수 있게 제약을 검
    // 클라이언트에서 페이징관련 옵션을 추가해서 api를 호출할 수 있게 됨
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim(); //사용자가 입력한 ticker 값
        if(ObjectUtils.isEmpty(ticker)){//빈 값 입력하면 오류
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());//회사를 저장할 때 마다 trie에 회사명이 저장됨
        return  ResponseEntity.ok(company);//회사 정보를 반환하는 것으로 함수를 종료 시킴
    }

    //회사 데이터 삭제
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")          //지워줄 회사명
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = this.companyService.deleteCompany(ticker);

        //캐시에서도 회사 데이터를 지워주어야 함
        this.clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);
    }

    //캐시에서도 회사 데이터를 지워주어야 함
    public void clearFinanceCache(String companyName){
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);

    }
}
