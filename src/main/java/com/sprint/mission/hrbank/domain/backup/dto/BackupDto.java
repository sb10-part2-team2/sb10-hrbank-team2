package com.sprint.mission.hrbank.domain.backup.dto;

import java.time.Instant;

public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    String status,
    Long fileId
) {

}
