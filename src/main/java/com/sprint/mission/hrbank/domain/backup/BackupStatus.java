package com.sprint.mission.hrbank.domain.backup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BackupStatus {
  IN_PROGRESS("진행중"),
  COMPLETED("완료"),
  FAILED("실패"),
  SKIPPED("건너뜀");

  @Getter
  private final String description;

  public String getCode() {
    return name();
  }
}
