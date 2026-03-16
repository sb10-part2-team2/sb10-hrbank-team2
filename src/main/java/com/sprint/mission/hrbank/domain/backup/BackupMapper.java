package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackupMapper {

  @Mapping(source = "backupFile.id", target = "fileId")
  BackupDto toDto(Backup backup);

  Backup toEntity(BackupDto backupDto);
}
