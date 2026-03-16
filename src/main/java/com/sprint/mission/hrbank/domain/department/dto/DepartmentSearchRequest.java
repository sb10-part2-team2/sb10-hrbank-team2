package com.sprint.mission.hrbank.domain.department.dto;

public record DepartmentSearchRequest(
    String nameOrDescription,
    Long idAfter,
    String cursor,
    Integer size,
    String sortField,
    String sortDirection
) {

  // Compact 생성자. 바인딩 직후 실행됨
  public DepartmentSearchRequest {
    if (size == null) {
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
