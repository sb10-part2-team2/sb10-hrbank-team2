package com.sprint.mission.hrbank.domain.department.dto;

import java.util.List;

public record CursorPageResponseDepartmentDto(
    List<DepartmentDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
