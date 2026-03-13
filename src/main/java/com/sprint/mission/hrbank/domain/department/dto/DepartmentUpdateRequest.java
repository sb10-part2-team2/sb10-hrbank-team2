package com.sprint.mission.hrbank.domain.department.dto;

public record DepartmentUpdateRequest(
    String name,
    String description,
    String establishedDate) {

}
