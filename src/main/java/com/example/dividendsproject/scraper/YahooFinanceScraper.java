package com.example.dividendsproject.scraper;

import com.example.dividendsproject.model.Company;
import com.example.dividendsproject.model.Dividend;
import com.example.dividendsproject.model.ScrapedResult;

import com.example.dividendsproject.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//09:25초 배당금저장03
@Component //빈으로 선언해서 사용하기 때문
public class YahooFinanceScraper implements Scraper {//frequency
//"https://finance.yahoo.com/quote/COKE/history?period1=99153000&period2=1716818154&frequency=1mo"
    //멤버변수로 빼놓으면 유지보수 관점에서 찾기쉽고 수정도 어렵지 않음. 메모리 관점에서도 좋음 배당금저장02
    //%s와 %d를 url에 추가한 이유는 stringformat 기능을 쓰기위함.scrap에서 URL호출을 통해서 문서를 받아올텐데 유동적으로 바뀔 수 있는 값들을 바꿔주기 위함
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&frequency=1mo"; // 문자열 s, 숫자d
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

    private static final long START_TIME = 86400; //60초*60분*24시간

    //스크랩 동작을 수행할 수 있는 스크랩 메소드
    @Override
    public ScrapedResult scrap(Company company){

        var scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis()/1000; //현재시간을 Millisecond로 받아서 초 단위로 바꾸기 해 1000으로 나눔

            //http 커넥을 맺음
            String url = String.format(STATISTICS_URL,company.getTicker(),START_TIME,now); //stringformat에서 첫번째 인자는 치환될 기본 베이스 문자열, 그 다음 치환될 값을 순서대로 넣어줌(ticker값 세팅, period1시작시간과 period2끝시간 임의로 세팅
            //connect 메소드는 Connection인스턴스 반환,커넥트의 파라미터는 우리가 요청할 url -> http connection 맺기
            Connection connection = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            //connection.get() http 커넥션을 맺은 값을 받아옴
            Document document = connection.get(); //Document인스턴스가 반환, html을 -> html문서를 받아서 파싱된 형태로 Document로 만들어주는 것을 json라이브러리가 해줌->회사, 배당금정보 가져옴

            //Elements 인스턴스 반환 -> 복수형, 이 속성을 가진 것이 하나가 아닐 수 있기 때문
            //파싱된 데이터에서 원하는 것을 추출
            Elements parsingDivs = document.select(".table.svelte-ewueuo");
            Elements  a = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0); //하지만 우리는 historical-prices을 가지고 있는 테이블 하나만 가져오기 때문에 get(0). table 전체

            Element tbody = tableEle.children().get(1); //table에서 thead, tbody, tfoot 순서대로 있음 children함수를 이용해 본문에 해당하는 tbody를 가져옴

            List<Dividend> dividends = new ArrayList<>();//dividend을 담을 LIST, 스크래핑 한 결과는 dividends에 담아서 scrapedResult에 최종결과로 반환
            for(Element e : tbody.children()){//tbody의 모든 데이터 순회
                //배당금 데이터만 가져오면 됨.element의 txt내용이 dividend으로 끝나는거만 보면 됨
                String txt = e.text();
                if(!txt.endsWith("Dividend")){//Dividend으로 끝나는 경우가 아니면 패스
                    continue;
                }

                //공백을 기준으로 split해서 순서대로 연월일과 배당금에 해당하는 부분을 찾아 변수에 대입
                String[] split = txt.split(" ");
                int month = Month.strToNumber(split[0]); //객체를 따로 생성하지 않고 strToNumber의 static으로 바로 호출
                int day = Integer.valueOf(split[1].replace(",",""));//day는 반점이 있어서 제거
                int year = Integer.valueOf(split[2]);
                String dividend = split[3];
                if(month < 0){
                    throw new RuntimeException("Unexpected Month enum value -> " + split[0]);
                }
                //dividend 아이템을 dividend 리스트에 추가->인스턴스가 생성되면서 바로 리스트에 추가됨
                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));

/*                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());*/


            }//이 for문을 돌면서 dividend을 돌면서 item을 하나씩 추가 종료되면
            scrapedResult.setDividends(dividends); // 리스트를 넣어줌

        } catch (IOException e){
            e.printStackTrace();
        }
        return scrapedResult;
    }//for문에 돌 때 마다 list에 dividend item이 하나씩 추가가 되고 종료되면 모든 아이템이 추가된 diviend list가 scrapedResult에 추가되서 결과로 리턴되고 함수가 종료됨

    @Override
    public Company scrapCompanyByTicker(String ticker){ //ticker을 받으면 그것에 해당하는 회사정보를 스크래핑을 찾아서 줌
        String url = String.format(SUMMARY_URL,ticker,ticker);

        try {
            Document document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();

/*           List<Element> titleList = document.getElementsByTag("h1");
            for(Element ele : titleList){
                System.out.println(ele.text());
            }*/

          Element titleEle = document.getElementsByTag("h1").get(1); //h1을 사용하는 태그에서 회사명 가져옴
            String title = titleEle.text();
            return new Company(ticker, title);
/*            return Company.builder().
                    ticker(ticker)
                    .name(title)
                    .build();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
