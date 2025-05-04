package com.serjlemast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponse handleNotFound(MethodArgumentNotValidException ex) {
    return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Validation problem")
        .title("Validation")
        .detail(ex.getMessage())
        .build();
  }
}
