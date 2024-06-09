package com.example.dividendsproject.service;

import com.example.dividendsproject.exception.impl.NoCompanyException;
import com.example.dividendsproject.model.Company;
import com.example.dividendsproject.model.Dividend;
import com.example.dividendsproject.model.ScrapedResult;
import com.example.dividendsproject.model.constants.CacheKey;
import com.example.dividendsproject.persist.CompanyRepository;
import com.example.dividendsproject.persist.DividendRepository;
import com.example.dividendsproject.persist.entity.CompanyEntity;
import com.example.dividendsproject.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    //redis를 함으로서 companyRepository와 dividendRepository에 따로 조회하지 않고
    //ubuntu@DESKTOP-94J8C23:~$ redis-cli
    //127.0.0.1:6379> keys *
    //1) "finance::3M Company (MMM)"
    //127.0.0.1:6379> get "finance::3M Company (MMM)"
    //이 과정에서 응답으로 나오는 값을 꺼내서 응답으로 내려줘야함
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)//캐싱 대상이 되는 메소드에 어노테이션을 붙이고 키와 값 설정
    public ScrapedResult getDividendByCompanyName(String companyName){
        //1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(()->new NoCompanyException());
        //orElseThrow는 값이 없으면 인자로 넘겨주는 예외를 발생시키고 있으면 Optional이 벗겨진
        // 알맹이 즉 CompanyEntity을 뱉어냄(findByName를 우리고 Opitonal로 받음)

        //2. 조회된 회사 ID 로 배당금 정보 조회

        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());
        //3. 결과 조합 후 반환
        //ScrapedResult는 company와 dividend도 entity타입이 아닌 일반 company와 dividend임
        //1번과 2번에서 가져온 값은 CompanyEntity와 DividendEntity 타입임->일반 모델 클래스로 맵핑해주는 작업을 해야함
        //DividendEntity는 List<DividendEntity>같이 List여서 가공해주는 작업이 필요함(2가지 방식-> for, string)
/*        List<Dividend> dividends = new ArrayList<>();
        for(var entity : dividendEntities){
            dividends.add(Dividend.builder()
                            .date(entity.getDate())
                            .dividend(entity.getDividend())
                    .build());
        }//dividends에는 dividendEntities의 값들이 Dividend모델 클래스로 값이 맵핑된 결과가 들어감*/

/*        List<Dividend> dividends2 = dividendEntities.stream()
                .map(e->Dividend.builder()
                                .date(e.getDate())
                                .dividend(e.getDividend())
                                .build())
                .collect(Collectors.toList());//값을 바꾸는 것이기에 map메서드 호출(entity하나하나 순환하면서 dividend모델에 맵핑된 결과를 넣음*/
        List<Dividend> dividends = dividendEntities.stream().map(e->new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);


/*        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(), dividends2);*/

    }
}