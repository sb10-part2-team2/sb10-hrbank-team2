package com.sprint.mission.hrbank.domain.employee.repository;

import com.sprint.mission.hrbank.domain.employee.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    EmployeeRepositoryCustom {

  long countAllByDepartmentId(Long departmentId);

  Optional<Employee> findById(long id);

  boolean existsByEmailAndIdNot(String email, Long id);

  boolean existsByEmail(String email);

}
