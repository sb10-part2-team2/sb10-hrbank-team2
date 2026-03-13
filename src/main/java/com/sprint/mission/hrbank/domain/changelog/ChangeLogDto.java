package com.sprint.mission.hrbank.domain.changelog;

import java.time.Instant;

// 목록 조회용
public record ChangeLogDto(
    Long id,
    String type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {

}