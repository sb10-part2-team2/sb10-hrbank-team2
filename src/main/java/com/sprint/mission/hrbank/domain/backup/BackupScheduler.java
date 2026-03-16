package com.sprint.mission.hrbank.domain.backup;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component // 스케줄링 작업을 진행할 클래스는 반드시 스프링 빈에 등록된 클래스여야 함
@RequiredArgsConstructor
public class BackupScheduler {

  private final BackupService backupService;

  // cron 방식이 아닌 interval-ms 방식 사용 -> 이전 작업 종료 후 1시간 뒤 발생
  // cron -> 실행 시점이 명확해야 할 때
  // interval-ms -> 작업 죵료 후 일정 간격 반복이 중요할 때
  @Scheduled(fixedDelayString = "${hrbank.backup.interval-ms}")
  public void runAutoBackUp() {
    backupService.createBackup("system"); // worker 이름 system으로 지정
  }
}
