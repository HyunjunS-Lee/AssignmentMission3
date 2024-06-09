package com.example.dividendsproject.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {//에러가 발생했을 때 던져줄 모델 클래스
    private int code;
    private String message;
}
