package com.example.dividendsproject.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//배당금 정보를 주고 받기 위한 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dividend { //이런 것들은 멤버변수가 많지 않아서 @Builder 어노테이션은 효율적이지 않다
//company이 클래스와 마친가지로 DividendEntity의 DividendId와 companyId가 빠짐
//com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.example.dividendsproject.model.ScrapedResult["dividends"]
// ->java.util.ArrayList[0]->com.example.dividendsproject.model.Dividend["date"]) 이 에러 해결
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    private String dividend;
}