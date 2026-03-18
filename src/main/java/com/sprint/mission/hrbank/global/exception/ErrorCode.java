package com.sprint.mission.hrbank.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // 백업

  // 변경로그
  CHANGE_LOG_NOT_FOUND(404, "해당하는 수정 이력이 없습니다"),
  CHANGE_LOG_INVALID_DATE_RANGE(400, "시작 일시는 종료 일시보다 이전이어야 합니다"),
  CHANGE_LOG_BEFORE_AFTER_REQUIRED(400, "이력을 생성하기 위한 직원 정보가 누락되었습니다"),
  CHANGE_LOG_UPDATED_REQUIRES_BOTH(400, "수정 이력을 생성하려면 수정 전/후 정보가 모두 필요합니다"),

  // 대시보드

  // 부서
  DEPARTMENT_NOT_FOUND(404, "해당하는 부서가 없습니다"),
  DEPARTMENT_NAME_DUPLICATE(400, "동일한 이름을 가진 부서가 있습니다"),
  DEPARTMENT_NOT_DELETABLE(400, "부서에 소속된 직원이 있어서 삭제할 수 없습니다"),

  // 직원
  EMAIL_ALREADY_EXISTS(400, "중복된 이메일입니다."),
  // 파일

  // 공용
  CLIENT_ERROR(400, "잘못된 요청입니다"),
  INTERNAL_SERVER_ERROR(500, "서버 오류입니다");

  private final int status;
  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

}
