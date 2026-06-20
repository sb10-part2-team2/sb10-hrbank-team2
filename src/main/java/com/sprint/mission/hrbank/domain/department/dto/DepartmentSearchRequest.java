package com.sprint.mission.hrbank.domain.department.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record DepartmentSearchRequest(
    @Schema(description = "부서 이름 또는 설명")
    String nameOrDescription,

    @Schema(description = "이전 페이지 마지막 요소 ID")
    Long idAfter,

    @Schema(description = "커서 (다음 페이지 시작점)")
    String cursor,

    @Schema(description = "페이지 크기 (기본값: 10, 최대 50)", defaultValue = "10")
    Integer size,

    @Schema(description = "정렬 필드 (name 또는 establishedDate)", defaultValue = "establishedDate")
    String sortField,

    @Schema(description = "정렬 방향 (asc 또는 desc, 기본값: asc)", defaultValue = "asc")
    String sortDirection
) {

  // Compact 생성자. 바인딩 직후 실행됨
  public DepartmentSearchRequest {
    if (size == null || size <= 0 || size > 50) {
      size = 10;
    }
    if (sortField == null) {
      sortField = "establishedDate";
    }
    if (sortDirection == null) {
      sortDirection = "asc";
    }
  }
}
