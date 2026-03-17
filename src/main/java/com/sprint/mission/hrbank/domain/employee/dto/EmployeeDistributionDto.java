package com.sprint.mission.hrbank.domain.employee.dto;

public record EmployeeDistributionDto(
    String groupKey,
    Long count,
    Double percentage
) {

}
