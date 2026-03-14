package com.sprint.mission.hrbank.domain.department.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DepartmentUpdateRequest(
    @NotBlank
    String name,

    @NotBlank
    String description,

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate establishedDate) {

}