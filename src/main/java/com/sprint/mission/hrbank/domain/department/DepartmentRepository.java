package com.sprint.mission.hrbank.domain.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

  boolean existsByName(String name);

  @Modifying(clearAutomatically = true)       // 쿼리 실행 후 1차 캐시 비워줌
  @Query("DELETE FROM Department d WHERE d.id = :id AND NOT EXISTS "
      + "(SELECT e FROM Employee e WHERE e.department.id = :id)")
  int deleteByIdIfNotEmployee(@Param("id") Long departmentId);
}
