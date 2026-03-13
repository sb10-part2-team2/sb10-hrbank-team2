package com.sprint.mission.hrbank.domain.department.dto;

public record DepartmentCreateRequest(
    String name,
    String description,
    String establishedDate) {

}
