package com.sprint.mission.hrbank.domain.employee.repository;

import com.sprint.mission.hrbank.domain.employee.EmployeeStatus;
import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCountRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDistributionDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendInterval;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepositoryCustom {

  CursorPageResponseEmployeeDto search(EmployeeSearchRequest req);

  long countEmployees(EmployeeCountRequest req);

  List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);

  List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, EmployeeTrendInterval interval);
}
