package com.sprint.mission.hrbank.domain.dashboard.controller;

import com.sprint.mission.hrbank.domain.dashboard.dto.response.EmployeeSummaryResponse;
import com.sprint.mission.hrbank.domain.dashboard.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashBoardController {

  private final DashBoardService dashBoardService;

  @GetMapping("/employees/summary")
  public ResponseEntity<EmployeeSummaryResponse> getEmployeeSummary() {
    EmployeeSummaryResponse dummydata = dashBoardService.getEmployeeSummary();
    return ResponseEntity.ok(dummydata);
  }
}
