package com.sprint.mission.hrbank.domain.backup;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BackupRepository extends JpaRepository<Backup, Long> {

  Optional<Backup> findFirstByStatusOrderByEndedAtDesc(BackupStatus status);

  // 실제 목록 데이터를 페이지 단위로 조회
  @Query("SELECT b FROM Backup b "
      + "WHERE (:idAfter IS NULL OR b.id < :idAfter) " // 커서 기반 페이징 (내림차순)
      + "AND (:worker IS NULL OR b.worker LIKE %:worker%) " // worker (부분일치)
      + "AND (CAST(:startedAtFrom AS timestamp) IS NULL OR b.startedAt >= :startedAtFrom) "
      // startedAt* (범위조건)
      + "AND (CAST(:startedAtTo AS timestamp) IS NULL OR b.startedAt <= :startedAtTo) "
      + "AND (:status IS NULL OR b.status = :status)" // status (완전일치)
  )
  Slice<Backup> searchBackupsDesc(
      @Param("worker") String worker,
      @Param("status") BackupStatus status,
      @Param("startedAtFrom") Instant startedAtFrom,
      @Param("startedAtTo") Instant startedAtTo,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );

  @Query("SELECT b FROM Backup b "
      + "WHERE (:idAfter IS NULL OR b.id > :idAfter) " // 커시 기반 페이징 (오름차순)
      + "AND (:worker IS NULL OR b.worker LIKE %:worker%) "
      + "AND (CAST(:startedAtFrom AS timestamp) IS NULL OR b.startedAt >= :startedAtFrom) "
      + "AND (CAST(:startedAtTo AS timestamp) IS NULL OR b.startedAt <= :startedAtTo) "
      + "AND (:status IS NULL OR b.status = :status)")
  Slice<Backup> searchBackupsAsc(
      @Param("worker") String worker,
      @Param("status") BackupStatus status,
      @Param("startedAtFrom") Instant startedAtFrom,
      @Param("startedAtTo") Instant startedAtTo,
      @Param("idAfter") Long idAfter,
      Pageable pageable
  );

  // 같은 필터 기준의 전체 건수를 계산 -> "총 xx"팀 정보를 위한 쿼리
  // TODO: 프론트 -> "총 xx건" 으로 수정 필요
  @Query("SELECT COUNT(b) FROM Backup b " // 검색 결과의 총 개수 가져옴
      + "WHERE (:worker IS NULL OR b.worker LIKE %:worker%) "
      + "AND (CAST(:startedAtFrom AS timestamp) IS NULL OR b.startedAt >= :startedAtFrom) "
      + "AND (CAST(:startedAtTo AS timestamp) IS NULL OR b.startedAt <= :startedAtTo) "
      + "AND (:status IS NULL OR b.status = :status)"
  )
  long countByConditions(
      @Param("worker") String worker,
      @Param("status") BackupStatus status,
      @Param("startedAtFrom") Instant startedAtFrom,
      @Param("startedAtTo") Instant startedAtTo
  );
}
