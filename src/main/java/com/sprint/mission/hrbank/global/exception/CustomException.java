package com.sprint.mission.hrbank.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final int status;
  private final String code;

  // 일반용
  public CustomException(ErrorCode e) {
    this(e, null);
  }

  // 추가 정보 기입용
  public CustomException(ErrorCode e, String detail) {
    super(detail != null ? e.getMessage() + "(" + detail + ")" : e.getMessage());
    this.status = e.getStatus();
    this.code = e.name();
  }
}
