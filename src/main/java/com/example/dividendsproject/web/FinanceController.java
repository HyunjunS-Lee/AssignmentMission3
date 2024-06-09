package com.example.dividendsproject.web;

import com.example.dividendsproject.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")//경로에 공통되는 부분은 여기로 뺄 수 있음 /finance/dividend/{companyName} -> /dividend/{companyName}
@AllArgsConstructor
public class FinanceController {

    private FinanceService financeService;

    //특정 회사의 배당금 조회
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        var result = this.financeService.getDividendByCompanyName(companyName);//ScrapResult
        return ResponseEntity.ok(result);
    }
}
