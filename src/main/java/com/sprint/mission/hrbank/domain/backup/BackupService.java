package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import com.sprint.mission.hrbank.domain.changelog.repository.ChangeLogRepository;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {

  private final BackupRepository backupRepository;
  private final ChangeLogRepository changeLogRepository;
  private final BackupCommandService backupCommandService;
  private final BackupCsvWriter backupCsvWriter;
  private final BackupMapper backupMapper;

  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public Backup createBackup(String workerIp) {
    // 1. 백업 필요 여부 판단
    Optional<Backup> last = backupRepository.findFirstByStatusOrderByEndedAtDesc(
        BackupStatus.COMPLETED);
    if (!checkDataChangedSince(last)) {
      return backupCommandService.createSkipped(workerIp);
    }

    // 2. IN_PROCESS 상태를 가진 백업 이력 생성 (트랜잭션 분리)
    Backup inProgress = backupCommandService.createInProgress(workerIp);

    try {
      // 3. csv 파일 생성
      StoredFile file = backupCsvWriter.writeEmployeeBackupCsv(
          inProgress.getId(),
          inProgress.getStartedAt()
      );

      // 4. [요구사항 Step 4-1] 성공 처리 (트랜잭션 분리)
      backupCommandService.markCompleted(inProgress.getId(), file);

    } catch (Exception e) {
      // 5. [요구사항 Step 4-2] 실패 처리
      StoredFile logFile = null;
      try {
        // 에러 로그 파일 생성
        logFile = backupCsvWriter.writeErrorLog(inProgress.getId(), workerIp,
            inProgress.getStartedAt(), e);
      } catch (Exception ignored) {
        // 에러 로그 파일 생성도 실패할 경우, 예외를 무시하고 백업 실패 처리 진행
      }

      // 에러 메시지 요약
      String errorSummary = summarizeException(e);
      // 최종 실패 상태 기록 (트랜잭션 분리)
      backupCommandService.markFailed(inProgress.getId(), logFile, errorSummary);
    }

    // 최종 상태 반영된 결과 조회해서 반환
    return backupRepository.findById(inProgress.getId())
        .orElseThrow(
            () -> new IllegalStateException("존재하지 않는 백업 이력입니다. ID: " + inProgress.getId()));
  }

  // 대시보드 마지막 백업 정보 조회용
  public Optional<BackupDto> getLatestBackup(BackupStatus status) {
    BackupStatus searchStatus = (status == null) ? BackupStatus.COMPLETED : status;
    return backupRepository.findFirstByStatusOrderByEndedAtDesc(searchStatus)
        .map(backupMapper::toDto);
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

  // 예외 메시지가 너무 길 경우 DB 저장 시 에러가 날 수 있어 안전하게 요약
  private String summarizeException(Exception e) {
    String message = e.getMessage();
    if (message == null) {
      return "Unknown Error occurred during backup.";
    }
    // 보통 DB의 String/Varchar 컬럼 크기인 255자를 기준으로 안전하게 200자 내외로 자름
    return message.length() > 200 ? message.substring(0, 200) + "..." : message;
  }
}
