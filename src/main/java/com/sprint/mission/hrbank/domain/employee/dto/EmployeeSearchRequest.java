package com.sprint.mission.hrbank.domain.employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sprint.mission.hrbank.domain.employee.EmployeeStatus;
import java.time.LocalDate;

public record EmployeeSearchRequest(String nameOrEmail,
                                    String departmentName,
                                    String position,
                                    String employeeNumber,

                                    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                    LocalDate hireDateFrom,

                                    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                                    LocalDate hireDateTo,
                                    EmployeeStatus status,
                                    String cursor,
                                    Long idAfter,
                                    int size) {

}
