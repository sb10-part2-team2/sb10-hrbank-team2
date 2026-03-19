package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/*
[백업 데이터의 상태 변경을 담당하는 커맨드 서비스]
- 실제 파일 생성 로직과 트랜잭션 분리
- 파일 생성 중 예외가 발생하더라도 백업 시작/실패 기록이 DB에 정상적으로 커밋되도록 Propagation.REQUIRES_NEW를 사용
*/

@Service
@RequiredArgsConstructor
public class BackupCommandService {

  private final BackupRepository backupRepository;

  // [백업 생성 시]
  // - 백업 시작 시 '진행 중' 상태 이력 생성
  // - 이미 진행 중인 백업이 있다면 예외 발생
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Backup createInProgress(String worker) {
    try {
      // 백업 생성 시도
      Instant before = Instant.now();
      return backupRepository.saveAndFlush(
          new Backup(worker, before, BackupStatus.IN_PROGRESS)
      );
    } catch (DataIntegrityViolationException e) {
      // 이미 진행 중인 백업이 있을 시 (유니크 제약 위반 발생 시) 예외 처리
      throw new IllegalStateException("이미 진행 중인 백업이 있습니다.", e);
    }
  }

  // [변경 사항이 없을 시] '건너뜀' 상태 이력 생성
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Backup createSkipped(String worker) {
    Instant before = Instant.now();
    return backupRepository.save(new Backup(worker, before, BackupStatus.SKIPPED));
  }

  // [백업이 성공적으로 완료되었을 시] 파일 정보와 완료 상태 업데이트
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markCompleted(Long backupId, StoredFile file) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 백업 이력입니다. ID: " + backupId));
    backup.markAsCompleted(file); // Backup 엔티티의 비즈니스 메서드 (백업 성공 시 상태를 COMPLETED로 변경하고 결과 파일 기록)
  }

  // [백업 중 예외 발생 시] 실패 상태와 에러 로그 정보 업데이트
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markFailed(Long backupId, StoredFile logFile, String summary) {
    Backup backup = backupRepository.findById(backupId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 백업 이력입니다. ID: " + backupId));
    backup.markAsFailed(logFile,
        summary); // Backup 엔티티의 비즈니스 메서드 (백업 실패 시 상태를 FAILED로 변경하고 에러 로그 파일과 요약 기록)
  }
}