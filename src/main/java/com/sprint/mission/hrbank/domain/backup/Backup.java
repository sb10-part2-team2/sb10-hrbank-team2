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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups", uniqueConstraints = {
    // status가 IN_PROGRESS인 데이터가 중복 저장되는 것을 DB 레벨에서 차단
    @UniqueConstraint(name = "uk_backup_in_progress", columnNames = {"in_progress_status"})
})
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

  // 진행 중일 때만 특정 값을 가지고, 완료되면 null이 되는 컬럼
  // 조회와 생성 사이의 Race Condition을 방지하기 위해 추가
  @Column(name = "in_progress_status")
  private String inProgressStatus;

  // 백업 상태 덮어쓰기를 방지하기 위해 추가
  @Version // JPA가 자동으로 관리하는 버전 컬럼
  private Long version;

  public Backup(String worker, Instant startedAt, BackupStatus status) {
    this.worker = worker;
    this.startedAt = startedAt;
    changeStatus(status);
  }

  // ----- 비즈니스 메서드 -----
  // 백업 성공 시, 상태를 COMPLETED로 변경하고 결과 파일 기록
  public void markAsCompleted(StoredFile backupFile) {
    changeStatus(BackupStatus.COMPLETED);
    this.endedAt = Instant.now();
    this.backupFile = backupFile;
  }

  // 백업 실패 시, 상태를 FAILED로 변경하고 에러 로그 파일과 요약 기록
  public void markAsFailed(StoredFile errorLogFile, String errorSummary) {
    changeStatus(BackupStatus.FAILED);
    this.endedAt = Instant.now();
    this.backupFile = errorLogFile;
    this.errorSummary = errorSummary;
  }

  // ----- 헬퍼 메서드 -----
  // 백업 상태 변경
  private void changeStatus(BackupStatus status) {
    if (this.status != null && this.status != BackupStatus.IN_PROGRESS) {
      throw new IllegalStateException("이미 종료된 백업 상태는 변경할 수 없습니다. 현재 상태: " + this.status);
    }
    this.status = status;
    this.inProgressStatus = (status == BackupStatus.IN_PROGRESS) ? "Y" : null;
  }
}
