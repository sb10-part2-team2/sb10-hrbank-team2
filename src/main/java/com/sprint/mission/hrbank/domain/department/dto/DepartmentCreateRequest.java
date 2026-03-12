package com.sprint.mission.hrbank.domain.department.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DepartmentCreateRequest {

  private final String name;
  private final String description;
  private final String establishedDate;

  public static DepartmentCreateRequest of(String name, String description,
      String establishedDate) {
    return new DepartmentCreateRequest(name, description, establishedDate);
  }
}
