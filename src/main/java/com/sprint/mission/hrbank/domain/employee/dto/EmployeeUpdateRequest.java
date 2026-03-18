package com.sprint.mission.hrbank.domain.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sprint.mission.hrbank.domain.employee.EmployeeStatus;
import java.time.LocalDate;

public record EmployeeUpdateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate hireDate,
    EmployeeStatus status,
    String memo
) {

}
