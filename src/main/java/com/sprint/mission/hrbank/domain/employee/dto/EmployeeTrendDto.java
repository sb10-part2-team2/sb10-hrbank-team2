package com.sprint.mission.hrbank.domain.employee.dto;

public record EmployeeTrendDto(
    String date,
    Long count,
    Long change,
    Double changeRate) {

}
