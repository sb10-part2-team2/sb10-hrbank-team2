package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
@Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
public class BackupController {

  private final BackupService backupService;
  private final BackupMapper backupMapper;

  // 데이터 백업 생성
  @PostMapping
  public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {
    // 클라이언트의 IP 주소 추출
    String clientIp = extractClientIp(request);

    // 백업 필요 여부 판단 및 이력 저장
    Backup backup = backupService.createBackup(clientIp);

    return ResponseEntity.ok(backupMapper.toDto(backup));
  }

  @GetMapping("/latest")
  @Operation(summary = "최근 백업 정보 조회", description = "지정된 상태의 가장 최근 백업 정보를 조회합니다. 상태를 지정하지 않으면 성공적으로 완료된(COMPLETED) 백업을 반환합니다.")
  public ResponseEntity<BackupDto> getLatestBackup(
      @Parameter(description = "백업 상태 (COMPLETED, FAILED, IN_PROGRESS, 기본값: COMPLETED)")
      @RequestParam(required = false) BackupStatus status) {
    return backupService.getLatestBackup(status)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.ok().build());
  }

  // ----- 헬퍼 메서드 -----
  // HttpServletRequest에서 클라이언트의 실제 IP를 추출하는 메서드
  private String extractClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty()) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
