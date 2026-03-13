package com.sprint.mission.hrbank.domain.employee.dto;

import com.sprint.mission.hrbank.domain.employee.Employee;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

}
