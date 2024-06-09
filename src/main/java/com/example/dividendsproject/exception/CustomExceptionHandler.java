package com.example.dividendsproject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice//필터와 비슷하고 컨트롤러 코드보다 좀 더 바깥쪽에서 동작(서비스에서 지정된 에러가 발생하면 그 에러를 잡아서 reponse로 던져줌
public class CustomExceptionHandler {
    @ExceptionHandler(AbstractException.class)//AbstractException가 발생한 경우
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException e) {
        //AbstractException가 발생했을 때 에러를 잡아서 어떻게 던져줄지 정해주면 됨
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getStatusCode())
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.resolve(e.getStatusCode()));
    }

}
