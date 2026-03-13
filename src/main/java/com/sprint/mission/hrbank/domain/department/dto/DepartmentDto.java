package com.sprint.mission.hrbank.domain.department.dto;

public record DepartmentDto(
    long id,
    String name,
    String description,
    String establishedDate,
    long employeeCount) {

}
