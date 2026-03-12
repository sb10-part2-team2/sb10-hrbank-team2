package com.sprint.mission.hrbank.domain.employee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EmployeeStatus {
  ACTIVE("재직중"),
  ON_LEAVE("휴직중"),
  RESIGNED("퇴사");

  @Getter
  private final String status;

  public String getCode() {
    return name();
  }


}
