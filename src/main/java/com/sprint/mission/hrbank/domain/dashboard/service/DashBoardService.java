package com.sprint.mission.hrbank.domain.dashboard.service;

import com.sprint.mission.hrbank.domain.dashboard.dto.response.EmployeeSummaryResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashBoardService {

  public EmployeeSummaryResponse getEmployeeSummary() {

    long totalEmployees = employeeRepository.countByStatusNot("RESIGNED");

    LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
    
    long newHiresThisMonth = employeeRepository.countByStatusNotAndHirDateGreaterThanEqual(
        "RESIGNED",
        firstDayOfMonth
    );

    return new EmployeeSummaryResponse(totalEmployees, newHiresThisMonth);
  }
}
