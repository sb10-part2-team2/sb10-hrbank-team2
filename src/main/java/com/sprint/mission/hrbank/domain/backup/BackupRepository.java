package com.sprint.mission.hrbank.domain.backup;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup, Long> {

  Optional<Backup> findFirstByStatusOrderByEndedAtDesc(BackupStatus status);

  Page<Backup> findAllByOrderByStartedAtDesc(Pageable pageable); // 전체 백업 이력 최신순 조회 (추후 페이징에 활용할 예정)

  Page<Backup> findAllByStatusOrderByStartedAtDesc(BackupStatus status,
      Pageable pageable); // 상태별 백업 이력 필터링 조회 (추후 페이징에 활용할 예정)

  boolean existsByStatus(BackupStatus status);
}
