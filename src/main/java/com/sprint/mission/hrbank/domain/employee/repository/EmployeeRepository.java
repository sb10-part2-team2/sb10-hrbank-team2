package com.sprint.mission.hrbank.domain.employee.repository;

import com.sprint.mission.hrbank.domain.employee.Employee;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    JpaSpecificationExecutor<Employee> {

  //    @Query("""
//        select e from Employee e
//        left join fetch e.department d
//                where :req.nameOrEmail
//        """
//    )
  Optional<Employee> findByRequest(EmployeeSearchRequest req);
}
