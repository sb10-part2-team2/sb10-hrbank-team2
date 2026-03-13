package com.sprint.mission.hrbank.domain.changelog;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

// 수정 이력 건수 날짜
public record ChangeLogCountRequest(
    Instant fromDate,
    Instant toDate
) {

  public ChangeLogCountRequest {
    if (fromDate == null) {
      fromDate = Instant.now().minus(7, ChronoUnit.DAYS);
    }
    if (toDate == null) {
      toDate = Instant.now();
    }
  }
}