package com.sprint.mission.hrbank.domain.file.repository;

import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<StoredFile, Long> {

}
