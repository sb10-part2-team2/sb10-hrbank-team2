package com.sprint.mission.hrbank.domain.department.dto;

import com.sprint.mission.hrbank.domain.department.Department;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DepartmentResponse {

  private final long id;
  private final String name;
  private final String description;
  private final String establishedDate;
  private final long employeeCount;

  public static DepartmentResponse toDto(Department department, long employeeCount) {
    return new DepartmentResponse(department.getId(), department.getName(),
        department.getDescription(), department.getEstablishedDate(), employeeCount);
  }
}
