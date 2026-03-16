package com.sprint.mission.hrbank.domain.dashboard.service;

import com.sprint.mission.hrbank.domain.backup.BackupMapper;
import com.sprint.mission.hrbank.domain.backup.BackupRepository;
import com.sprint.mission.hrbank.domain.backup.BackupStatus;
import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import com.sprint.mission.hrbank.domain.changelog.ChangeLogCountRequest;
import com.sprint.mission.hrbank.domain.changelog.ChangeLogRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

  private final ChangeLogRepository changeLogRepository;
  private final BackupRepository backupRepository;
  private final BackupMapper backupMapper;

  public long getChangeLogsCount(ChangeLogCountRequest request) {
    return changeLogRepository.countChangeLogs(request.fromDate(), request.toDate());
  }

  public Optional<BackupDto> getLatestBackup(BackupStatus status) {
    BackupStatus searchStatus = (status == null) ? BackupStatus.COMPLETED : status;
    return backupRepository.findFirstByStatusOrderByEndedAtDesc(searchStatus)
        .map(backupMapper::toDto);
  }
}
