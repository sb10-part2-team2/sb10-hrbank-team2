package com.sprint.mission.hrbank.domain.employee.repository;

import com.sprint.mission.hrbank.domain.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    EmployeeRepositoryCustom {

}
