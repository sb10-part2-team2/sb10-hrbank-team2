package com.sprint.mission.hrbank.domain.changelog.mapper;

import com.sprint.mission.hrbank.domain.changelog.ChangeLog;
import com.sprint.mission.hrbank.domain.changelog.ChangeLogDiff;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogDetailDto;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogDto;
import com.sprint.mission.hrbank.domain.changelog.dto.DiffDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ChangeLogMapper {

  @Mapping(source = "employeeNumberSnapshot", target = "employeeNumber")
  @Mapping(source = "createdAt", target = "at")
  ChangeLogDto toDto(ChangeLog changeLog);

  @Mapping(source = "employeeNumberSnapshot", target = "employeeNumber")
  @Mapping(source = "createdAt", target = "at")
  @Mapping(source = "employeeNameSnapshot", target = "employeeName")
  @Mapping(source = "profileImageIdSnapshot", target = "profileImageId")
  @Mapping(source = "changeLogDiffs", target = "diffs")
  ChangeLogDetailDto toDetailDto(ChangeLog changeLog);

  @Mapping(source = "beforeValue", target = "before")
  @Mapping(source = "afterValue", target = "after")
  DiffDto toDiffDto(ChangeLogDiff diff);
}

