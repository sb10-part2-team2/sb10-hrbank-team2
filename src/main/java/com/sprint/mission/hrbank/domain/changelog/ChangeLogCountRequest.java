package com.sprint.mission.hrbank.domain.changelog;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record ChangeLogCountRequest(
    Instant fromDate,
    Instant toDate
) {

  public ChangeLogCountRequest {
    if (toDate == null) {
      toDate = Instant.now();
    }
    if (fromDate == null) {
      fromDate = toDate.minus(7, ChronoUnit.DAYS);
    }

    if (fromDate.isAfter(toDate)) {
      throw new IllegalArgumentException("시작 일시는 종료 일시보다 이전이어야 합니다.");
    }
  }
}