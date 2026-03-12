package com.sprint.mission.hrbank.domain.department.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DepartmentUpdateRequest {

  private final String name;
  private final String description;
  private final String establishedDate;

  public static DepartmentUpdateRequest of(String name, String description,
      String establishedDate) {
    return new DepartmentUpdateRequest(name, description, establishedDate);
  }
}
