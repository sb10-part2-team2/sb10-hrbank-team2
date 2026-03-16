package com.sprint.mission.hrbank.domain.department;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
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

  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      + "WHERE (d.name like %:keyword% OR d.description like %:keyword%) "
      + "AND (:cursor IS NULL OR "
      + " (d.name > :cursor OR "
      + " (d.name = :cursor AND (:idAfter IS NULL OR d.id > :idAfter)))) "
      + "GROUP BY d "
      + "ORDER BY d.name ASC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByNameAsc(
      @Param("keyword") String keyword,
      @Param("cursor") String cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);

  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      + "WHERE (d.name like %:keyword% OR d.description like %:keyword%) "
      + "AND (:cursor IS NULL OR "
      + " (d.name < :cursor OR "
      + " (d.name = :cursor AND (:idAfter IS NULL OR d.id < :idAfter)))) "
      + "GROUP BY d "
      + "ORDER BY d.name DESC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByNameDesc(
      @Param("keyword") String keyword,
      @Param("cursor") String cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);

  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      + "WHERE (d.name like %:keyword% OR d.description like %:keyword%) "
      + "AND (:cursor IS NULL OR "
      + " (d.establishedDate > :cursor OR "
      + " (d.establishedDate = :cursor AND (:idAfter IS NULL OR d.id > :idAfter)))) "
      + "GROUP BY d "
      + "ORDER BY d.establishedDate ASC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByEstablishedDateAsc(
      @Param("keyword") String keyword,
      @Param("cursor") LocalDate cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);

  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      + "WHERE (d.name like %:keyword% OR d.description like %:keyword%) "
      + "AND (:cursor IS NULL OR "
      + " (d.establishedDate < :cursor OR "
      + " (d.establishedDate = :cursor AND (:idAfter IS NULL OR d.id < :idAfter)))) "
      + "GROUP BY d "
      + "ORDER BY d.establishedDate DESC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByEstablishedDateDesc(
      @Param("keyword") String keyword,
      @Param("cursor") LocalDate cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);
}
