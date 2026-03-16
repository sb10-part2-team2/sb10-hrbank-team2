package com.sprint.mission.hrbank.domain.dashboard.controller;

import com.sprint.mission.hrbank.domain.changelog.ChangeLogCountRequest;
import com.sprint.mission.hrbank.domain.dashboard.service.DashBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
public class DashBoardController {

  private final DashBoardService dashboardService;

  @Operation(summary = "수정 이력 건수 조회", description = "직원 정보 수정 이력 건수를 조회합니다. 파라미터를 제공하지 않으면 최근 일주일 데이터를 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 날짜 범위"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @GetMapping("/api/change-logs/count")
  public ResponseEntity<Long> getChangeLogsCount(ChangeLogCountRequest request) {
    long count = dashboardService.getChangeLogsCount(request);
    return ResponseEntity.ok(count);
  }
}
