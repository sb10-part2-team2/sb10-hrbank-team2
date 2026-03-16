package com.sprint.mission.hrbank.domain.employee.repository;

import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;

public interface EmployeeRepositoryCustom {

  CursorPageResponseEmployeeDto search(EmployeeSearchRequest req);
}
