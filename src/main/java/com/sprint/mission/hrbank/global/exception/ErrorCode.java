package com.sprint.mission.hrbank.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // 백업

  // 변경로그

  // 대시보드

  // 부서

  // 직원
  EMAIL_ALREADY_EXISTS(400, "중복된 이메일입니다."),
  // 파일

  // 공용
  INTERNAL_SERVER_ERROR(500, "서버 오류입니다");

  private final int status;
  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

}
