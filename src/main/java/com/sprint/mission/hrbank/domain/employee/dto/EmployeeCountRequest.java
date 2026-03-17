package com.sprint.mission.hrbank.domain.employee.dto;

import com.sprint.mission.hrbank.domain.employee.EmployeeStatus;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record EmployeeCountRequest(
    EmployeeStatus status,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fromDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate toDate
) {
}
