package com.sprint.mission.hrbank.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeDistributionResponse {

  private String label;

  private long count;

  private double percentage;
}
