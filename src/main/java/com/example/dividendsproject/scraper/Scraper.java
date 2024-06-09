package com.example.dividendsproject.scraper;

import com.example.dividendsproject.model.Company;
import com.example.dividendsproject.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
//야후 파이낸스 사이트에서만 스크래핑 해오지만 naver에서 스크래핑해와서 더 많은 정보를 추가해주거나
//혹은 추가하지 않고 야후가 아닌 다른 사이트에서 스크래핑 해오는 걸로 서비스를 변경할 수 있음
//원래 코드에서 그런 것을 하면 코드에 많은 공수가 들어감.. 코드의 재사용성과 확정성을 높이기 위해 범용성 있는 이 언터페이스를 생성함
//YahooFinanceScraper는 Scraper 인터페이스를 구현해준 형태로 만들어줬음(오버라이드)
//이제 NAVER FINANCE에서도 스크래핑 하고 싶으면 YahooFinanceScraper처럼 클래스를 생성하여 implements Scraper을 해주어 오버라이드하면 됨
//그렇게 하여 서비스 확장성을 가져간다.
//메인에서도 YahooFinanceScraper scraper = new YahooFinanceScraper(); 가 아닌
// Scraper scraper = new YahooFinanceScraper(); 이렇게 함