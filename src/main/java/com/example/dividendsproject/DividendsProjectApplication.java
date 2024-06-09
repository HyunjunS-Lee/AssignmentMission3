package com.example.dividendsproject;

import com.example.dividendsproject.model.Company;
import com.example.dividendsproject.scraper.Scraper;
import com.example.dividendsproject.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling //스케쥴링 사용
@EnableCaching
public class DividendsProjectApplication {

    public static void main(String[] args) {
               SpringApplication.run(DividendsProjectApplication.class, args);


//임의의 문자열 생성
//        String s = "hello my name is grace";
//        String s1 = "hello my name is grace1";
//        String s2 = "hello my name is grace2";
//        System.out.println(s);
//        System.out.println(s1);
//        System.out.println(s2);
//        ->이렇게 하면 코드의 중복은 물론 코드가 동작되는 시험(호출되는)에 회사ticker을 받아와서 스크랩핑이 실행되어야하는 로직에서는 이렇게(s,s1,s2) 문자열을 주문 못함->이럴 때 stringformat
        //File -> Settings -> Editor -> File Encodings에서 Project Encoding, Default encoding for properties files를 UTF-8로 설정해서 해결

/*        String s = "hello my name is %s";
        String[] names = {"grace", "grace1", "grace2"};
        for(String name : names) {
            System.out.println(String.format(s, name));
        }*/


/*        var result = scraper.scrap(Company.builder().ticker("COKE").build());
        System.out.println(result);*/

/*        //여기 테스트입니다!!
        var result = scraper.scrapCompanyByTicker("MMM");
        System.out.println(result);*/


    }
}
