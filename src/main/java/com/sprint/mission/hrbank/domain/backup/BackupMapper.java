package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BackupMapper {

  @Mapping(target = "fileId", ignore = true)
    // @Mapping(source = "backupFile.id", target = "fileId") // 추후 파일 연동 시 변경 예정
  BackupDto toDto(Backup backup);

  Backup toEntity(BackupDto backupDto);
}
