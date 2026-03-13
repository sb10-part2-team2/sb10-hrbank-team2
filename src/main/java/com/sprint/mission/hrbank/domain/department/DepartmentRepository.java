package com.sprint.mission.hrbank.domain.department;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

  boolean existsByName(String name);
}
