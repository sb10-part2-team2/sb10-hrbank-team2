package com.sprint.mission.hrbank.domain.dashboard.controller;

import com.sprint.mission.hrbank.domain.dashboard.dto.response.EmployeeSummaryResponse;
import com.sprint.mission.hrbank.domain.dashboard.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashBoardController {

  private final DashBoardService dashBoardService;

  @GetMapping("api/employees/count")
  public ResponseEntity<EmployeeSummaryResponse> getEmployeeSummary() {
    EmployeeSummaryResponse dummydata = dashBoardService.getEmployeeSummary();
    return ResponseEntity.ok(dummydata);
  }
}
