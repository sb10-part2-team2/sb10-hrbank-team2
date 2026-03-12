package com.sprint.mission.hrbank.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeSummaryResponse {

  private long totalEmployees;

  private long newHiresThisMonth;
}
