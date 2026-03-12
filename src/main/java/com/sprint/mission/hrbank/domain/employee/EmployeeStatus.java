package com.sprint.mission.hrbank.domain.employee;

import lombok.Getter;

@Getter
public enum EmployeeStatus {
    ACTIVE("재직중"),
    ON_LEAVE("휴직중"),
    RESIGNED("퇴사");

    final private String name;

    EmployeeStatus(String name) {
        this.name = name;
    }


}
