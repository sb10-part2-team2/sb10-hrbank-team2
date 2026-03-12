package com.sprint.mission.hrbank.domain.employee.dto;

import java.time.Instant;

public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    Long departmentId,
    String departmentName,
    String position,
    Instant hireDate,
    String status,
    Long profileImageId
) {

}
