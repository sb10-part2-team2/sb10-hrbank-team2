package com.sprint.mission.hrbank.domain.changelog.controller;

import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogCountRequest;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogDetailDto;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogSearchRequest;
import com.sprint.mission.hrbank.domain.changelog.dto.CursorPageResponseChangeLogDto;
import com.sprint.mission.hrbank.domain.changelog.service.ChangeLogService;
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
public class ChangeLogController {

  private final ChangeLogService changeLogService;


  // 수정 이력 목록 조회
  @GetMapping
  public ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
      @ModelAttribute ChangeLogSearchRequest request) {
    return ResponseEntity.ok(changeLogService.getChangeLogs(request));
  }

  // 상세 이력 목록 조회
  @GetMapping(value = "/{id}")
  public ResponseEntity<ChangeLogDetailDto> getChangeLogDetail(
      @PathVariable Long id) {
    return ResponseEntity.ok(changeLogService.getChangeLogDetail(id));
  }

  // 수정 이력 건수 조회
  @GetMapping(value = "/count")
  public ResponseEntity<Long> getChangeLogCount(
      @ModelAttribute ChangeLogCountRequest request) {
    return ResponseEntity.ok(
        changeLogService.getChangeLogCount(request.fromDate(), request.toDate()));
  }

}
