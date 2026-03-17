package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

  Department toEntity(DepartmentCreateRequest request);

  DepartmentDto toDto(Department department, long employeeCount);
}
