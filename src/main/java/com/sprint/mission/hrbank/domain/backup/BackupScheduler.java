package com.sprint.mission.hrbank.domain.backup;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component // 스케줄링 작업을 진행할 클래스는 반드시 스프링 빈에 등록된 클래스여야 함
@RequiredArgsConstructor
public class BackupScheduler {

  private final BackupService backupService;
  private static final Logger log = LoggerFactory.getLogger(BackupScheduler.class); // 로그 작성용

  // cron 방식이 아닌 interval-ms 방식 사용
  // - 서버 기동 시 즉시 1회 실행 (initialDelay 기본값 = 0)
  // - 이후 이전 작업 종료 후 interval-ms(기본 1시간) 간격으로 반복 실행
  // cron -> 실행 시점이 명확해야 할 때
  // interval-ms -> 작업 죵료 후 일정 간격 반복이 중요할 때
  @Scheduled(fixedDelayString = "${hrbank.backup.interval-ms}")
  public void runAutoBackUp() {
    try {
      backupService.createBackup("system"); // worker 이름 system으로 지정
    } catch (RuntimeException e) { // 실패 시
      log.error("자동 백업 스케줄 실행 실패: ", e); // 로그 기록
    }
  }
}
