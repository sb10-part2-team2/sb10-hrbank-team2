package com.sprint.mission.hrbank.domain.changelog.controller;

import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogCountRequest;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogDetailDto;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogSearchRequest;
import com.sprint.mission.hrbank.domain.changelog.dto.CursorPageResponseChangeLogDto;
import com.sprint.mission.hrbank.domain.changelog.service.ChangeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
public class ChangeLogController {

  private final ChangeLogService changeLogService;


  // 수정 이력 목록 조회
  @Operation(summary = "직원 정보 수정 이력 목록 조회", description = "직원 정보 수정 이력 목록을 조회합니다. 상세 변경 내용은 포함되지 않습니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 정렬 필드")
  @ApiResponse(responseCode = "500", description = "서버 오류")
  @GetMapping
  public ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
      @ModelAttribute ChangeLogSearchRequest request) {
    return ResponseEntity.ok(changeLogService.getChangeLogs(request));
  }

  // 상세 이력 목록 조회
  @Operation(summary = "직원 정보 수정 이력 상세 조회", description = "직원 정보 수정 이력의 상세 정보를 조회합니다. 변경 상세 내용이 포함됩니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @ApiResponse(responseCode = "404", description = "이력을 찾을 수 없음")
  @ApiResponse(responseCode = "500", description = "서버 오류")
  @GetMapping(value = "/{id}")
  public ResponseEntity<ChangeLogDetailDto> getChangeLogDetail(
      @PathVariable Long id) {
    return ResponseEntity.ok(changeLogService.getChangeLogDetail(id));
  }

  // 수정 이력 건수 조회
  @Operation(summary = "수정 이력 건수 조회", description = "직원 정보 수정 이력 건수를 조회합니다. 파라미터를 제공하지 않으면 최근 일주일 데이터를 반환합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 날짜 범위")
  @ApiResponse(responseCode = "500", description = "서버 오류")
  @GetMapping(value = "/count")
  public ResponseEntity<Long> getChangeLogCount(
      @ModelAttribute ChangeLogCountRequest request) {
    return ResponseEntity.ok(
        changeLogService.getChangeLogCount(request.fromDate(), request.toDate()));
  }

}
