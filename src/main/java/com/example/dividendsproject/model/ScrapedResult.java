package com.example.dividendsproject.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//스크랩핑한 결과를 주고 받기 위한 클래스
@Data
@AllArgsConstructor //모든 필드를 초기화 하는 생성자 코드 사용
public class ScrapedResult {

    private  Company company;//스크랩핑한 회사 어떤 회사인지 정보를 담은 Company인스턴스

    //배당금 인스턴스 리스트를 멤버변수로 가짐->한 회사는 여러개의 배당금 정보를 가지고 있어서
    private List<Dividend> dividends;

    public ScrapedResult() {this.dividends = new ArrayList<>();}
}
