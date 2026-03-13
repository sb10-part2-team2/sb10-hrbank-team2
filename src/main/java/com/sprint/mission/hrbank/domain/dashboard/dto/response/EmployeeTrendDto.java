package com.sprint.mission.hrbank.domain.dashboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeTrendDto {

  private String date;
  private long count;
  private long change;
  private double changeRate;
}
