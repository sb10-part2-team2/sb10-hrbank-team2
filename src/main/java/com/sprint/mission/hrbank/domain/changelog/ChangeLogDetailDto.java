package com.sprint.mission.hrbank.domain.changelog;

import java.time.Instant;
import java.util.List;

// 직원 정보 수정 이력 상세 조회
public record ChangeLogDetailDto(
    Long id,
    String type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at,
    String employeeName,
    Long profileImageId,
    List<DiffDto> diffs
) {

}