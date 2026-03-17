package com.sprint.mission.hrbank.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.ConstraintViolation;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
@JsonPropertyOrder({"timestamp", "status", "message", "details"})   // 반환필드 순서 고정
public class ErrorResponse<T> {

  private final Instant timestamp;
  private final int status;
  private final String message;
  private final T details;

  private ErrorResponse(int status, String message, T details) {
    this.timestamp = Instant.now();
    this.status = status;
    this.message = message;
    this.details = details;
  }

  public static ErrorResponse<List<ErrorDetail>> of(int status, String message,
      BindingResult bindingResult) {
    return new ErrorResponse<>(status, message, ErrorDetail.of(bindingResult));
  }

  public static ErrorResponse<List<ErrorDetail>> of(int status, String message,
      Set<ConstraintViolation<?>> constraintViolations) {
    return new ErrorResponse<>(status, message, ErrorDetail.of(constraintViolations));
  }

  public static ErrorResponse<String> of(int status, String message, String details) {
    return new ErrorResponse<>(status, message, details);
  }

  @Getter
  @JsonPropertyOrder({"name", "rejectedValue", "reason"})
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ErrorDetail {

    private final String name;
    private final Object rejectedValue;
    private final String reason;

    public static List<ErrorDetail> of(BindingResult bindingResult) {
      return bindingResult.getFieldErrors().stream()
          .map(err -> new ErrorDetail(
              err.getField(),
              err.getRejectedValue(),
              err.getDefaultMessage()))
          .toList();
    }

    public static List<ErrorDetail> of(Set<ConstraintViolation<?>> constraintViolations) {
      return constraintViolations.stream()
          .map(cv -> new ErrorDetail(
              cv.getPropertyPath().toString(),
              cv.getInvalidValue(),
              cv.getMessage()))
          .collect(Collectors.toList());
    }
  }
}
