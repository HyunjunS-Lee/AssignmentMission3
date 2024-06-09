package com.example.dividendsproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data //getter setter tostring equal hashcode requireargumentconstructor 포하함한 어노테이션
//@Builder //디자인 패턴 중 builder패턴이 있는데 해당 클래스에서 사용하게 해줌
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    //CompanyEntity클래스에서 id만 뺀 클래스->CompanyEntity사용하지 않고 모델클래스를 따라 정의해준 이유는
    //Entity는 db와 직접적으로 맵핑되기 위한 클래스여서 Entity인스턴스를 서비스 코드 내부에서 데이터를 주고받는 용도나
    //이 과정에서 data를 변경하는 로직을 넣으면 CompanyEntity의 클래스 원래 역할 범위를 벗어남->
    // 코드의 재사용성 개념x 자기 역할을 벗어난 역할을 하게 되면 좋지못한 신호.. 본래 자기역할에만 충실해야함

    private String ticker;
    private String name;
}