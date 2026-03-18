package com.sprint.mission.hrbank.domain.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EmployeeCreateRequest(
    @NotBlank
    String name,

    @NotBlank
    String email,

    @NotNull
    Long departmentId,

    @NotBlank
    String position,

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate hireDate,

    String memo) {

}
