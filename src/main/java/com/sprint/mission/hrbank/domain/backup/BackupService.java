package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.changelog.repository.ChangeLogRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {

  private final BackupRepository backupRepository;
  private final ChangeLogRepository changeLogRepository;

  @Transactional
  public Backup createBackup(String workerIp) {
    // 가장 최근에 성공한 백업 이력 조회
    Optional<Backup> lastCompletedBackup = backupRepository.findFirstByStatusOrderByEndedAtDesc(
        BackupStatus.COMPLETED);

    // 최근 백업 이후 데이터 변경이 있었는지 확인
    boolean needsBackup = checkDataChangedSince(lastCompletedBackup);

    // 변경 사항 없으면 SKIPPED 상태 저장
    if (!needsBackup) {
      Backup skippedBackup = new Backup(workerIp, Instant.now(), BackupStatus.SKIPPED);
      return backupRepository.save(skippedBackup);
    }

    // 변경 사항 있으면 IN_PROGRESS 상태 저장
    Backup inProgressBackup = new Backup(workerIp, Instant.now(), BackupStatus.IN_PROGRESS);
    return backupRepository.save(inProgressBackup);
  }

  // ----- 헬퍼 메서드 -----
  // 백업 시점 이후에 데이터 변경이 있었는지 판단
  private boolean checkDataChangedSince(Optional<Backup> lastBackup) {
    if (lastBackup.isEmpty() || lastBackup.get().getEndedAt() == null) {
      return true;
    }

    Instant lastBackupTime = lastBackup.get().getEndedAt();
    Instant now = Instant.now();

    // countChangeLogs를 활용하여 변경 이력 존재 여부 확인
    long changedCount = changeLogRepository.countChangeLogs(lastBackupTime, now);
    return changedCount > 0;
  }
}
