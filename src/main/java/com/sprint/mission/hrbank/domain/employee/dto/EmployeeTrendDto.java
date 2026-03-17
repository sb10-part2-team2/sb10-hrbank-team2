package com.sprint.mission.hrbank.domain.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record EmployeeTrendDto(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate date,
    Long count,
    Long change,
    Double changeRate) {

}
