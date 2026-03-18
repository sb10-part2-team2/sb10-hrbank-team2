package com.sprint.mission.hrbank.domain.changelog.repository;

import com.sprint.mission.hrbank.domain.changelog.ChangeLog;
import com.sprint.mission.hrbank.domain.changelog.ChangeLogType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

  // 이력 목록 조회 (필터 + 커서 기반 idAfter)
  @Query("SELECT c FROM ChangeLog c "
      + "WHERE (:idAfter IS NULL OR c.id < :idAfter) " // 커서 기반 페이지네이션(최신순)
      + "AND (:employeeNumber IS NULL OR c.employeeNumberSnapshot LIKE %:employeeNumber%) "
      // 사번 부분 일치
      + "AND (:memo IS NULL OR c.memo LIKE %:memo%) " // 메모 부분 일치
      + "AND (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%) "  // ip 부분 일치
      + "AND (:type IS NULL OR c.type = :type) " // 유형 완전 일치
      + "AND (CAST(:atFrom AS timestamp) IS NULL OR c.createdAt >= :atFrom) " // 시간 범위 ~부터
      + "AND (CAST(:atTo AS timestamp) IS NULL OR c.createdAt <= :atTo)")
  // 시간 범위 ~까지
  Slice<ChangeLog> searchChangeLogs(
      @Param("employeeNumber") String employeeNumber,
      @Param("type") ChangeLogType type,
      @Param("memo") String memo,
      @Param("ipAddress") String ipAddress,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo,
      @Param("idAfter") Long idAfter,
      Pageable pageable // Sort 포함
  );

  @Query("SELECT COUNT(c) FROM ChangeLog c "
      + "WHERE (:employeeNumber IS NULL OR c.employeeNumberSnapshot LIKE %:employeeNumber%) "
      + "AND (:type IS NULL OR c.type = :type) "
      + "AND (:memo IS NULL OR c.memo LIKE %:memo%) "
      + "AND (:ipAddress IS NULL OR c.ipAddress LIKE %:ipAddress%) "
      + "AND (CAST(:atFrom AS timestamp) IS NULL OR c.createdAt >= :atFrom) "
      + "AND (CAST(:atTo AS timestamp) IS NULL OR c.createdAt <= :atTo)")
  long countByConditions(
      @Param("employeeNumber") String employeeNumber,
      @Param("type") ChangeLogType type,
      @Param("memo") String memo,
      @Param("ipAddress") String ipAddress,
      @Param("atFrom") Instant atFrom,
      @Param("atTo") Instant atTo
  );

  // 상세 조회 (diff 같이 조회)
  @Query("SELECT c FROM ChangeLog c "
      + "LEFT JOIN FETCH c.changeLogDiffs WHERE c.id = :id")
  Optional<ChangeLog> findDetailById(@Param("id") Long id);

  // 수정 이력 건수 조회
  @Query("SELECT COUNT(c) FROM ChangeLog c "
      + "WHERE c.createdAt BETWEEN :fromDate AND :toDate")
  long countChangeLogs(
      @Param("fromDate") Instant fromDate,
      @Param("toDate") Instant toDate
  );

  @Query("""
          SELECT c FROM ChangeLog c 
          WHERE c.employeeId = :employeeId
      """)
  List<ChangeLog> findByEmployeeId(@Param("employeeId") long id);
}
