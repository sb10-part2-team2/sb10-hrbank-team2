package com.sprint.mission.hrbank.domain.dashboard.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashBoardService {

  public long getChangeLogsCount(String fromDateStr, String toDateStr) {

    LocalDateTime fromDate;
    LocalDateTime toDate;

    if (toDateStr == null || toDateStr.isBlank()) {
      toDate = LocalDateTime.now();
    } else {
      toDate = LocalDateTime.parse(toDateStr, DateTimeFormatter.ISO_DATE_TIME);
    }

    if (fromDateStr == null || fromDateStr.isBlank()) {
      fromDate = toDate.minusDays(7);
    } else {
      fromDate = LocalDateTime.parse(fromDateStr, DateTimeFormatter.ISO_DATE_TIME);
    }
    return 12L;
  }
}
