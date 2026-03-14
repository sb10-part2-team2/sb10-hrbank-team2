package com.sprint.mission.hrbank.domain.employee.dto;


public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    Long departmentId,
    String departmentName,
    String position,
    String hireDate,
    String status,
    Long profileImageId
) {

}
