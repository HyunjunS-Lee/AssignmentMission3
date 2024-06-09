package com.example.dividendsproject.persist.entity;


import com.example.dividendsproject.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

//회사 데이터를 맵핑하기 위한 CompanyEntity -> 즉 domain 날씨 일기 프로젝트 참고
@Entity(name = "COMAPNY") //테이블명은 COMAPNY
@Getter //멤버 변수 가져오기
@ToString //인스턴스 출력을 위한 편의
@NoArgsConstructor //생성자를 만들어주는데 생성자에 argument가 하나도 없는 생성자
public class CompanyEntity {

    @Id //이 값을 아디로 쓰는다는 것을 알려주기 위해
    @GeneratedValue(strategy = GenerationType.IDENTITY) //아이디가 생성되는 것을 autoInclement로 하기로 함
    private Long id; //아이디

    @Column(unique = true)//중복이 되면 안됨
    private String ticker;

    private String name;//회사명 저장

    public CompanyEntity(Company company){
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
