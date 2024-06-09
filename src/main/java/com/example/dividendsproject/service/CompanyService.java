package com.example.dividendsproject.service;

import com.example.dividendsproject.exception.impl.NoCompanyException;
import com.example.dividendsproject.model.Company;
import com.example.dividendsproject.model.ScrapedResult;
import com.example.dividendsproject.persist.CompanyRepository;
import com.example.dividendsproject.persist.DividendRepository;
import com.example.dividendsproject.persist.entity.CompanyEntity;
import com.example.dividendsproject.persist.entity.DividendEntity;
import com.example.dividendsproject.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.imageio.plugins.tiff.TIFFDirectory;
import java.util.List;
import java.util.stream.Collectors;

@Service//싱글톤으로 관리됨 -> 프로그램이 실행되는 동안 CompanyService인스턴스는 하나만 생성이 되고 한 인스턴스만 사용됨
@AllArgsConstructor//빈이 생성될 때 사용될 수 있도록 함
public class CompanyService {
//스크래퍼로 가져온 데이터를 저장해줌

    //private final Trie trie = new PatriciaTrie<>(); Appconfig를 만들지 않고 이렇게 초기화 해줘도 되지만 trie는 서비스 내에서 하나만 유지가 되어야하고 코드의 일관성 유지를 위해 bean으로 관리
    private final Trie trie;//Appconfig에서 생성된 trie빈이 초기화 될 때 CompanyService에 주입이 되면서 CompanyService의 Trie인스턴스로 사용
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker){
        boolean exists = this.companyRepository.existsByTicker(ticker);//회사 존재 여부 boolean값으로 확인
        if(exists){
            throw  new RuntimeException("already exists ticker -> " + ticker); //존재하면 존재한다고 알림
        }
        return this.storeCompanyAndDividend(ticker); //존재하지 않으면 받은 결과값을 반환

    }

    //회사 조회
    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return this.companyRepository.findAll(pageable);//pageable을 넘겨주게 되면 List가 아닌 pageEntity타입을 받음
    }

    //이것은 우리가 db에 저장하지 않은 회사 정보를 storeCompanyAndDividend통해 저장
    private Company storeCompanyAndDividend(String ticker){//저장한 티커에 해당하는 회사 인스턴스를 반환
        //ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)){//회사 정보가 있는지 없는지
            throw new RuntimeException("failed to scrap ticker -> "  + ticker);
        }
        //해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);
        //스크래핑 결과
        //company entity를 저장할 건데 companyRepository에 저장되는 타입은 companyEntity타입이 저장되어야함.
        // -> Company 인스턴스를 companyEntity로 바꿔줄건데 이 과정을 편하기 위해 companyEntity에 생성자 추가함
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        //map은 e(element)를 다른 값으로 매핑을 해주기위해서 작업
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e-> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());//결과 값을 지정한 list타입으로 반환하게 해줌
        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }

    //like연산자 사용
    public List<String> getCompanyNamesByKeyword(String keyword){
        //모든 회사를 조회하지 않고 회수 갯수에 제한을 걸기 위해 Pageable사용
        Pageable limit = PageRequest.of(0,10); //10개씩
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()//companyEntities에 있는 회사명 추출
                .map(e->e.getName())
                .collect(Collectors.toList());
    }

    //회사명을 저장하는 메서드
    public void addAutocompleteKeyword(String keyword){
//apache에서 구현된 trie는 기본적인 형태라기 보다 이런저런 기능을 붙여서 사용될 수 있게
// 응용된 형태로 key,value로 저장할 수 있게 구현되어있음
//일단 value로 해당하는 값을 넣을 것 없이 자동완성 기능만 구현을 위해 null로 처리
        this.trie.put(keyword, null);//회사명 저장
    }

    //회사명 조회하는 메서드
    public List<String> autocomplete(String keyword){
        //keySet을 list형태로 반환
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream().collect(Collectors.toList());
    }

    //trie에 저장된 키워드를 삭제하는 메서드
    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker){//회사를 지우고 회사이름 반환
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(()->new NoCompanyException());

        //companyID가 가지고 있는 회사 배당금 데이터 다 지우기
        this.dividendRepository.findAllByCompanyId(company.getId());

        //companyRepository에 있는 company지우기
        this.companyRepository.delete(company);

        //자동완성기능 trie에서도 지워져야함
        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
