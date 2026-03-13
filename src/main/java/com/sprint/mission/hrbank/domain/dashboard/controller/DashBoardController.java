package com.sprint.mission.hrbank.domain.dashboard.controller;

import com.sprint.mission.hrbank.domain.dashboard.dto.response.EmployeeTrendDto;
import com.sprint.mission.hrbank.domain.dashboard.service.DashBoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashBoardController {

  private final DashBoardService dashboardService;

  @GetMapping("/api/employees/stats/trend")
  public ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
      @RequestParam(required = false) String from,
      @RequestParam(required = false) String to,
      @RequestParam(defaultValue = "month") String unit
  ) {
    List<EmployeeTrendDto> response = dashboardService.getEmployeeTrend(from, to, unit);

    return ResponseEntity.ok(response);
  }
}
