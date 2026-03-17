package com.sprint.mission.hrbank.global.exception;

import com.sprint.mission.hrbank.global.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j                                // 예상치 못한 예외처리 로그용
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 예상치 못한 전역예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse<?>> handleException(Exception e) {
    log.error("Unhandled Exception occurred: ", e);
    ErrorResponse<?> errorResponse = ErrorResponse.of(500, "INTERNAL_SERVER_ERROR", "서버 오류입니다");
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // DTO 예외
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse<?>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse<?> errorResponse = ErrorResponse.of(
        400, "VALIDATION_ERROR", e.getBindingResult());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // 파라미터 예외
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse<?>> handleConstraintViolationException(
      ConstraintViolationException e) {
    ErrorResponse<?> errorResponse = ErrorResponse.of(
        400, "CONSTRAINT_VIOLATION", e.getConstraintViolations());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // Custom 예외
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse<?>> handleConstraintViolationException(
      CustomException e) {
    ErrorResponse<?> errorResponse = ErrorResponse.of(
        // 시연용 swagger에서는 message는 Exception 이름, details는 예외 일반내용 이렇게 정의되어 있어서
        // Exception 이름을 제거하고 대신 CODE로 변경하기로 합의되었습니다
        e.getStatus(), e.getCode(), e.getMessage());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }
}
