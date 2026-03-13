package com.sprint.mission.hrbank.domain.backup.repository;

import com.sprint.mission.hrbank.domain.backup.entity.Backup;
import com.sprint.mission.hrbank.domain.backup.entity.BackupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup, Long> {

  Page<Backup> findAllByOrderByStartedAtDesc(Pageable pageable); // 전체 백업 이력 최신순 조회

  Page<Backup> findAllByStatusOrderByStartedAtDesc(BackupStatus status,
      Pageable pageable); // 상태별 백업 이력 필터링 조회
}
