package com.sprint.mission.hrbank.domain.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record DepartmentCreateRequest(
    @NotBlank
    String name,

    @NotBlank
    String description,

    @NotNull
    @DateTimeFormat(pattern = "YYYY-MM-DD")
    LocalDate establishedDate) {

}
