package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
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
