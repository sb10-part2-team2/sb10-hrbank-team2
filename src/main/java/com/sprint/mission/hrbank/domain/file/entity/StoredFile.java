package com.sprint.mission.hrbank.domain.file.entity;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO: 참조하는 곳들 File -> StoredFile 로 수정
@Entity
@Table(name = "files")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoredFile extends BaseEntity {

  // id

  // 사용자 다운로드 표시용 이름
  @Column(nullable = false)
  private String originalName;

  // 서버에 저장할 고유 파일명
  @Column(nullable = false, unique = true)
  private String storedName;

  @Column(nullable = false)
  private String contentType;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false)
  private String storagePath;

  // createdAt

  public static StoredFile create(
      String originalName,
      String storedName,
      String contentType,
      Long size,
      String storagePath
  ) {
    StoredFile file = new StoredFile();
    file.setOriginalName(originalName);
    file.setStoredName(storedName);
    file.setContentType(contentType);
    file.setSize(size);
    file.setStoragePath(storagePath);
    return file;
  }
}
