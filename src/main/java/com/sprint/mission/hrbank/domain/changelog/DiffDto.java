package com.sprint.mission.hrbank.domain.changelog;

// 변경된 필드 하나하나의 정보
public record DiffDto(
    String propertyName,
    String before,
    String after
) {

}