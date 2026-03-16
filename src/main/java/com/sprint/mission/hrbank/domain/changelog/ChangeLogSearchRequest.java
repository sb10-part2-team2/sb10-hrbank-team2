package com.sprint.mission.hrbank.domain.changelog;

import java.time.Instant;

// 직원 정보 수정 이력 목록 조회 요청 데이터
public record ChangeLogSearchRequest(
    String employeeNumber,
    ChangeLogType type,
    String memo,
    String ipAddress,
    Instant atFrom,
    Instant atTo,
    Long idAfter,
    String cursor,
    Integer size,         // 초기화 로직을 위해 Integer 사용 가능
    String sortField,
    String sortDirection
) {

  // Compact Constructor: 레코드 생성 시 로직 추가
  public ChangeLogSearchRequest {
    // null이거나 값이 비어있을 경우 기본값 할당
    if (size == null || size <= 0) {
      size = 10;
    }
    if (sortField == null || sortField.isBlank()) {
      sortField = "at";
    }
    if (sortDirection == null || sortDirection.isBlank()) {
      sortDirection = "desc";
    }
  }
}