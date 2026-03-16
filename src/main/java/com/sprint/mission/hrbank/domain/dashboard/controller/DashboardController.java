package com.sprint.mission.hrbank.domain.dashboard.controller;

import com.sprint.mission.hrbank.domain.backup.BackupStatus;
import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogCountRequest;
import com.sprint.mission.hrbank.domain.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @Operation(summary = "수정 이력 건수 조회", description = "직원 정보 수정 이력 건수를 조회합니다. 파라미터를 제공하지 않으면 최근 일주일 데이터를 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 날짜 범위"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
  @GetMapping("/api/change-logs/count")
  public ResponseEntity<Long> getChangeLogsCount(@ModelAttribute ChangeLogCountRequest request) {
    long count = dashboardService.getChangeLogsCount(request);
    return ResponseEntity.ok(count);
  }

  @Operation(summary = "최근 백업 정보 조회", description = "지정된 상태의 가장 최근 백업 정보를 조회합니다. 상태를 지정하지 않으면 성공적으로 완료된(COMPLETED) 백업을 반환합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 상태값"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  @Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
  @GetMapping("/api/backups/latest")
  public ResponseEntity<BackupDto> getLatestBackup(
      @Parameter(description = "백업 상태 (COMPLETED, FAILED, IN_PROGRESS, 기본값: COMPLETED)")
      @RequestParam(required = false) BackupStatus status) {
    return dashboardService.getLatestBackup(status)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.ok().build());
  }
}
