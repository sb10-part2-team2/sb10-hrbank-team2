package com.sprint.mission.hrbank.domain.changelog;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ChangeLogMapper {

  // Entity -> 목록 응답 DTO
  public ChangeLogDto toResponse(ChangeLog entity) {
    return new ChangeLogDto(
        entity.getId(),
        entity.getType().name(),
        entity.getEmployeeNumberSnapshot(),
        entity.getMemo(),
        entity.getIpAddress(),
        entity.getCreatedAt()
    );
  }

  // Entity -> 상세 목록 응답 DTO
  public ChangeLogDetailDto toDetailResponse(ChangeLog changeLog) {
    List<DiffDto> diffs = changeLog.getChangeLogDiffs().stream()
        .map(ChangeLogMapper::toDiffResponse)
        .toList();

    return new ChangeLogDetailDto(
        changeLog.getId(),
        changeLog.getType().name(),
        changeLog.getEmployeeNumberSnapshot(),
        changeLog.getMemo(),
        changeLog.getIpAddress(),
        changeLog.getCreatedAt(),
        changeLog.getEmployeeNameSnapshot(),   // 엔티티 자체 필드 사용
        changeLog.getProfileImageIdSnapshot(), // 엔티티 자체 필드 사용
        diffs
    );
  }

  // 수정 사항 DTO
  public static DiffDto toDiffResponse(ChangeLogDiff diff) {
    return new DiffDto(
        diff.getPropertyName(),
        diff.getBeforeValue(),
        diff.getAfterValue()
    );
  }
}
