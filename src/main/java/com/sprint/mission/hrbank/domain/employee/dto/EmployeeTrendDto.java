package com.sprint.mission.hrbank.domain.employee.dto;

import java.time.Instant;

public record EmployeeTrendDto(
    Instant date,
    Long count,
    Long change,
    Double changeRate) {

}
