package com.example.dividendsproject.scheduler;


import com.example.dividendsproject.model.Company;
import com.example.dividendsproject.model.ScrapedResult;
import com.example.dividendsproject.model.constants.CacheKey;
import com.example.dividendsproject.persist.CompanyRepository;
import com.example.dividendsproject.persist.DividendRepository;
import com.example.dividendsproject.persist.entity.CompanyEntity;
import com.example.dividendsproject.persist.entity.DividendEntity;
import com.example.dividendsproject.scraper.Scraper;
import com.example.dividendsproject.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j//로깅 기능 사용
@Component
@EnableCaching
@AllArgsConstructor//REPOSITORY가 초기화 될 수 있도록
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;;
    private final DividendRepository dividendRepository;

    //일정 주기마다 수행
    //스크래핑 주기 변경-> 이것을 위해 코드의 빌드 배포과정을 매번 거치는 것은 비효율적->
    // 서비스 제공 중 변경 여지가 있는 cron스케쥴러 값 같은 경우는 config로 관리해주는 것이 효율
    //allEntries-> redis 캐시에 있는 finance에 해당하는 데이터는 모두 다 비움, 특정 키만 key = ""
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)//value는 redis에 데이터가 저장되었을 때 redis서버의 키의 프리패스로 사용
    @Scheduled(cron = "${scheduler.scrap.yahoo}")//매일 정각에 실행
    public void yahooFinanceScheduling(){//Scheduled가 동작할 때 마다 CacheEvict도 같이 동작하면서 캐시에 있는 데이터도 비워짐->다시 회사 배당금을 조회하는 시점에 캐시에 데이터가 올라감
        log.info("scraping scheduler is started");
        //저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        //회사 하나씩 순회하면서 회사에 대한 배당금 정보를 새로 스크래핑
        for(var company : companies){
            log.info(company.getName());
            //companies는 CompanyEntity이므로 company bulider로 company인스턴스에 맵핑
            ScrapedResult scrapResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));
/*            ScrapedResult scrapResult = this.yahooFinanceScraper.scrap(Company.builder()
                    .name(company.getName())
                    .ticker(company.getTicker()).build());*/

            //스크래핑한 정보 중에 db에 없는 값은 저장
            scrapResult.getDividends().stream()                      //e는 dividend모델값, 즉 디비든 모델을 디비든 엔티티로 맵핑
                    .map(e-> new DividendEntity(company.getId(), e)) //map을 이렇게 해주면 DividendEntity 생성자를 통해서 맵핑됨
                    .forEach(e->{ //foreach에서는 element를 하나씩 디비든 레파지토리에 존재하지 않으면 삽입
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());//존재 확인
                        if(!exists){//존재하지 않으면 DividendEntity를 저장
                            this.dividendRepository.save(e);
                            log.info("insert new dividend = " + e.toString());
                        }
                    });

            //연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지-> 일정 시간을 주지 않고 보내면 db에 부하가 감
            try {
                Thread.sleep(30000); //3초를 의미
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();//적절한 조치
            }
        }

    }
}
