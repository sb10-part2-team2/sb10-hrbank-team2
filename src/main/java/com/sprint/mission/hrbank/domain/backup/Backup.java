package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Column(nullable = false, length = 50)
  private String worker;

  @Column(nullable = false)
  private Instant startedAt;

  @Column
  private Instant endedAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BackupStatus status;

//  @ManyToOne(fetch = FetchType.LAZY) // 추후 파일 엔티티 연동 시 추가
//  @JoinColumn(name = "backup_file_id")
//  private File backupFile;

  @Column(columnDefinition = "TEXT")
  private String errorSummary;

  public Backup(String worker, Instant startedAt, BackupStatus status) {
    this.worker = worker;
    this.startedAt = startedAt;
    this.status = status;
  }

//  public void markAsCompleted(File backupFile) { // 추후 파일 엔티티 연동 시 추가
//    this.status = BackupStatus.COMPLETED;
//    this.endedAt = Instant.now();
//    this.backupFile = backupFile;
//  }
//
//  public void markAsFailed(File errorLogFile, String errorSummary) { // 추후 파일 엔티티 연동 시 추가
//    this.status = BackupStatus.FAILED;
//    this.endedAt = Instant.now();
//    this.backupFile = errorLogFile;
//    this.errorSummary = errorSummary;
//  }
}
