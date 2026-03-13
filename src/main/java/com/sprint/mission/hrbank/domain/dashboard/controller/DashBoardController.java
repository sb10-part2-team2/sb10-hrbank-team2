package com.sprint.mission.hrbank.domain.dashboard.controller;

import com.sprint.mission.hrbank.domain.dashboard.dto.response.EmployeeDistributionResponse;
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

  private final DashBoardService dashboadService;

  @GetMapping("api/employees/stats/distribution")
  public ResponseEntity<List<EmployeeDistributionResponse>> getEmployeeDistribution(
      @RequestParam(defaultValue = "department") String groupBy
  ) {
    List<EmployeeDistributionResponse> response = dashboadService.getEmployeeDistribution(groupBy);
    
    return ResponseEntity.ok(response);
  }
}
