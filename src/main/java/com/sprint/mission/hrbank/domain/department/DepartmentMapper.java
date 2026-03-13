package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

  @Mapping(target = "establishedDate", source = "establishedDate", qualifiedByName = "toInstant")
  Department toEntity(DepartmentCreateRequest request);

  @Mapping(target = "establishedDate", source = "department.establishedDate", qualifiedByName = "toStringDate")
  DepartmentDto toDto(Department department, long employeeCount);

  @Named("toInstant")
  default Instant toInstant(String timeString) {
    if (timeString == null) {
      return null;
    }

    return LocalDate.parse(timeString)
        .atStartOfDay(ZoneId.of("Asia/Seoul"))
        .toInstant();
  }

  @Named("toStringDate")
  default String toString(Instant instant) {
    if (instant == null) {
      return null;
    }

    return instant.atZone(ZoneId.of("Asia/Seoul"))
        .toLocalDate()
        .toString();
  }
}
