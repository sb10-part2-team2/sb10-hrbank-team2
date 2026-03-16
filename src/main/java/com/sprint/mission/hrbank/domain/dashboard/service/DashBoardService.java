package com.sprint.mission.hrbank.domain.dashboard.service;

import com.sprint.mission.hrbank.domain.changelog.ChangeLogCountRequest;
import com.sprint.mission.hrbank.domain.changelog.ChangeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashBoardService {

  private final ChangeLogRepository changeLogRepository;

  public long getChangeLogsCount(ChangeLogCountRequest request) {
    return changeLogRepository.countChangeLogs(request.fromDate(), request.toDate());
  }
}
