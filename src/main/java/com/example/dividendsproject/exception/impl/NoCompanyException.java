package com.example.dividendsproject.exception.impl;

import com.example.dividendsproject.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoCompanyException extends AbstractException {

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();//400번대에 해당하는 코드
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 회사명 입니다";
    }
}
