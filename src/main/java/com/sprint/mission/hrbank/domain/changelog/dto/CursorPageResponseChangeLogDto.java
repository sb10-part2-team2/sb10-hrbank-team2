package com.sprint.mission.hrbank.domain.changelog.dto;

import java.time.Instant;
import java.util.List;

// 목록 조회 페이지 응답
public record CursorPageResponseChangeLogDto(
    List<ChangeLogDto> content,
    Instant nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}