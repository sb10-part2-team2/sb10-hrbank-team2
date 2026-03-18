package com.sprint.mission.hrbank.domain.backup.dto;

import com.sprint.mission.hrbank.domain.backup.BackupStatus;
import java.time.Instant;

// 백업 목록 조회용 dto
public record BackupSearchRequest(
    String worker, // 작업자
    BackupStatus status, // 상태 (IN_PROGRESS, COMPLETED, FAILED)
    Instant startedAtFrom, // 시작 시간(부터)
    Instant startedAtTo, // 시작 시간(까지)
    Long idAfter, // 이전 페이지 마지막 요소 ID
    String cursor, // 커서 (이전 페이지의 마지막 ID)
    Integer size, // 페이지 크기
    String sortField, // 정렬 필드 (startedAt, endedAt, status)
    String sortDirection // 정렬 방향 (ASC, DESC)
) {

}
