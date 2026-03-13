package com.sprint.mission.hrbank.domain.dashboard.controller;

import com.sprint.mission.hrbank.domain.dashboard.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashBoardController {

  private final DashBoardService dashboardService;
  
  @GetMapping("/api/change-logs/count")
  public ResponseEntity<Long> getChangeLogsCount(
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate
  ) {
    long count = dashboardService.getChangeLogsCount(fromDate, toDate);
    return ResponseEntity.ok(count);
  }
}
