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

  // 이름 기반 오름차순 정렬로 부서와 직원수 쿼리
  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      // 키워드 X or 이름 or 설명의 부분검색
      + "WHERE (:keyword IS NULL OR :keyword = '' OR d.name like %:keyword% OR d.description like %:keyword%) "
      // 커서가 없거나
      + "AND (:cursor IS NULL OR "
      // 커서보다 크거나
      + " (d.name > :cursor OR "
      // 커서가 같지만, ID보다 큰 경우
      + " (d.name = :cursor AND d.id > :idAfter))) "
      + "GROUP BY d "
      + "ORDER BY d.name ASC, d.id ASC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByNameAsc(
      @Param("keyword") String keyword,
      @Param("cursor") String cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);

  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      + "WHERE (:keyword IS NULL OR :keyword = '' OR d.name like %:keyword% OR d.description like %:keyword%) "
      + "AND (:cursor IS NULL OR "
      + " (d.name < :cursor OR "
      + " (d.name = :cursor AND (:idAfter IS NULL OR d.id < :idAfter)))) "
      + "GROUP BY d "
      + "ORDER BY d.name DESC, d.id DESC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByNameDesc(
      @Param("keyword") String keyword,
      @Param("cursor") String cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);

  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      + "WHERE (:keyword IS NULL OR :keyword = '' OR d.name like %:keyword% OR d.description like %:keyword%) "
      + "AND (:cursor IS NULL OR "
      + " (d.establishedDate > :cursor OR "
      + " (d.establishedDate = :cursor AND d.id > :idAfter))) "
      + "GROUP BY d "
      + "ORDER BY d.establishedDate ASC, d.id ASC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByEstablishedDateAsc(
      @Param("keyword") String keyword,
      @Param("cursor") LocalDate cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);

  @Query("SELECT d, COUNT(e) FROM Department d "
      + "LEFT JOIN Employee e ON d.id = e.department.id "
      + "WHERE (:keyword IS NULL OR :keyword = '' OR d.name like %:keyword% OR d.description like %:keyword%) "
      + "AND (:cursor IS NULL OR "
      + " (d.establishedDate < :cursor OR "
      + " (d.establishedDate = :cursor AND (:idAfter IS NULL OR d.id < :idAfter)))) "
      + "GROUP BY d "
      + "ORDER BY d.establishedDate DESC, d.id DESC")
  List<Object[]> findByNameContainingOrDescriptionContainingOrderByEstablishedDateDesc(
      @Param("keyword") String keyword,
      @Param("cursor") LocalDate cursor,
      @Param("idAfter") Long idAfter,
      Pageable pageable);

  @Query("SELECT COUNT(d) FROM Department d "
      + "WHERE d.name like %:keyword% OR d.description like %:keyword%")
  long countByNameOrDescriptionContaining(@Param("keyword") String keyword);
}
