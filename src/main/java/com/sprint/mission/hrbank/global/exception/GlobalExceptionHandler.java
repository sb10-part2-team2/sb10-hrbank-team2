package com.sprint.mission.hrbank.global.exception;

import com.sprint.mission.hrbank.global.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j                                // 예상치 못한 예외처리 로그용
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // 예상치 못한 전역예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse<?>> handleException(Exception e) {
    log.error("Unhandled Exception occurred: ", e);
    ErrorResponse<?> errorResponse = ErrorResponse.of(500, "INTERNAL_SERVER_ERROR", "서버 오류입니다");
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // DTO 예외
  @Override
  protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status, @NonNull WebRequest request) {

    ErrorResponse<?> errorResponse = ErrorResponse.of(
        400, "VALIDATION_ERROR", ex.getBindingResult());

    return handleExceptionInternal(ex, errorResponse, headers, status, request);
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
  public ResponseEntity<ErrorResponse<?>> handleCustomException(CustomException e) {
    ErrorResponse<?> errorResponse = ErrorResponse.of(
        // 시연용 swagger에서는 message는 Exception 이름, details는 예외 일반내용 이렇게 정의되어 있어서
        // Exception 이름을 제거하고 대신 CODE로 변경하기로 합의되었습니다
        e.getStatus(), e.getCode(), e.getMessage());
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }

  // ResponseEntityExceptionHandler의 Spring 전체 에러핸들용. 500이 될 것을 400번대로 반환
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      @NonNull Exception ex, Object body, @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status, @NonNull WebRequest request) {

    Object errorResponse = (body instanceof ErrorResponse) ? body :
        ErrorResponse.of(status.value(), "CLIENT_ERROR", "요청 처리 중 오류가 발생했습니다");

    return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
  }
}
