package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Backup extends BaseEntity {

  // 작업자
  @Column(nullable = false, length = 50)
  private String worker;

  // 백업 작업이 시작된 시각
  @Column(nullable = false)
  private Instant startedAt;

  // 백업 작업이 종료된 시각
  @Column
  private Instant endedAt;

  // 백업 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BackupStatus status;

  // 백업 결과로 생성된 파일
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "backup_file_id")
  private StoredFile backupFile;

  // 백업 실패 시 발생한 에러 요약 정보
  @Column(columnDefinition = "TEXT")
  private String errorSummary;

  public Backup(String worker, Instant startedAt, BackupStatus status) {
    this.worker = worker;
    this.startedAt = startedAt;
    this.status = status;
  }

  // ----- 비즈니스 메서드 -----
  // 백업 성공 시, 상태를 COMPLETED로 변경하고 결과 파일 기록
  public void markAsCompleted(StoredFile backupFile) {
    this.status = BackupStatus.COMPLETED;
    this.endedAt = Instant.now();
    this.backupFile = backupFile;
  }

  // 백업 실패 시, 상태를 FAILED로 변경하고 에러 로그 파일과 요약 기록
  public void markAsFailed(StoredFile errorLogFile, String errorSummary) {
    this.status = BackupStatus.FAILED;
    this.endedAt = Instant.now();
    this.backupFile = errorLogFile;
    this.errorSummary = errorSummary;
  }
}
