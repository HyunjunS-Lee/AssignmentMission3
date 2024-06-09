package com.example.dividendsproject.persist.entity;

import com.example.dividendsproject.model.Dividend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

//배당금 맵핑을 위한 DividendEntity
@Entity(name = "DIVIDEND")//테이블명은 DIVIDEND
@Getter //멤버 변수 가져오기
@ToString //인스턴스 출력을 위한 편의
@NoArgsConstructor //생성자를 만들어주는데 생성자에 argument가 하나도 없는 생성자
//유니크 키는 일종의 인덱스이자 제약조건(db의 인덱스와 list의 인덱스는 다른 개념)
//배당금 정보가 중복으로 저장되는 것을 막기 위해 복합유니크 키 설정
//즉 유니크 키는 테이블이나 인덱스에 같은 값이 2개 이상 저장될 수  없도록 함
//지금 제가 한것은 companyId와 date 값이 동일하다면 중복되서 저장x
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"companyId", "date"})})//이건 복합컬럼.. 단일 컬럼도 가능
public class DividendEntity {

    @Id //이 값을 아디로 쓰는다는 것을 알려주기 위해
    @GeneratedValue(strategy = GenerationType.IDENTITY) //아이디가 생성되는 것을 autoInclement로 하기로 함
    private Long id;

    private Long companyId; //배당금 데이터가 어떤 회사의 배당금 정보인지 저장할 수 있는 회사 아이디

    private LocalDateTime date; //배당금 날짜 저장

    private String dividend; //배당금 금액이 얼마였는지

    //모델 인스턴스를 Entity인스턴스로 바꾸는 것을 수월하게 해줌.
    public DividendEntity(Long companyId, Dividend dividend){
        this.companyId = companyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }

}
