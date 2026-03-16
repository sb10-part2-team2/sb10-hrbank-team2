package com.sprint.mission.hrbank.domain.employee.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    Long departmentId,
    String departmentName,
    String position,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate hireDate,
    String status,
    Long profileImageId
) {

}
