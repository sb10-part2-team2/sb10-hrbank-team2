package com.sprint.mission.hrbank.domain.file.service;

import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import com.sprint.mission.hrbank.domain.file.repository.FileRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// TODO: 저장해야 되는 파일 -> 프로필 이미지, 전체 직원 정보 CSV 파일, 에러 로그 .log 파일
@Service
public class FileService {

  private final Path rootPath;
  private final FileRepository fileRepository;

  public FileService(
      @Value("${hrbank.storage.local.root-path}") Path rootPath,
      FileRepository fileRepository
  ) {
    this.rootPath = Path.of(rootPath.toString());
    this.fileRepository = fileRepository;
    try {
      Files.createDirectories(this.rootPath.resolve("files"));
      // resolve() : 경로 합치기
    } catch (IOException e) {
      throw new IllegalStateException("상위 디렉터리를 생성하는데 실패했습니다", e);
    }
  }

  // CREATE
  // 실제 파일 저장 + 메타데이터 저장 -> 사진 저장
  @Transactional
  public StoredFile saveData(MultipartFile multipartFile) {
    if (multipartFile == null || multipartFile.isEmpty()) {
      throw new IllegalArgumentException("업로드할 파일이 없거나 비어 있습니다");
    }

    String originalFilename = multipartFile.getOriginalFilename();
    if (originalFilename == null || originalFilename.isBlank()) { // 내용 없는 이름 필터링
      throw new IllegalArgumentException("유효한 파일명이 없습니다");
    }
    originalFilename = Paths.get(originalFilename).getFileName().toString();
    if (originalFilename.isBlank()) { // / . .. 케이스 필티링
      throw new IllegalArgumentException("유효한 파일명이 없습니다");
    }
    // 파일 이름 고유성 확보
    String storedName = UUID.randomUUID() + "-" + originalFilename;
    Path targetPath = rootPath.resolve("files").resolve(storedName);

    // 경로가 예상 디렉터리 내에 있는지 검증 - 보안 관련
    // targetPath.normalize() -> 절대 경로로 변환
    // startsWith(rootPath.resolve("files")) -> 최종 경로가 허용한 디렉터리안에서 시작하는지 확인
    if (!targetPath.normalize().startsWith(rootPath.resolve("files"))) {
      throw new IllegalArgumentException("잘못된 파일 경로입니다");
    }

    try {
      multipartFile.transferTo(targetPath); // transferTo: 파일을 경로에 저장
    } catch (IOException e) {
      throw new IllegalStateException("로컬 디스크에 파일 저장 중 오류가 발생하였습니다: " + e);
    }

    StoredFile file = StoredFile.create(
        originalFilename,
        storedName,
        multipartFile.getContentType(),
        multipartFile.getSize(),
        targetPath.toString()
    );

    try {
      return fileRepository.save(file);
    } catch (RuntimeException e) { // 만약 메타데이터 정보 저장 중 예외 발생시
      try {
        Files.deleteIfExists(targetPath); // 데이터 일치를 위해 실제 파일 삭제 시도
      } catch (IOException cleanupEx) { //
        e.addSuppressed(cleanupEx); // 추가로 발생한 예외들 하나의 예외 객체 안에 묶어서 관리
      }
      throw e;
    }
  }

  // 백업 파일 저장 -> 로그 + CSV
  // TODO: CSV로 저장? -> 프로젝트 분석 필요
  @Transactional
  public StoredFile saveBackupData(String filename, String content, String contentType) {
    if (filename == null || filename.isBlank()) {
      throw new IllegalArgumentException("유효한 파일명이 없습니다");
    }
    // 전체 경로에서 파일 이름(확장자 포함)만을 추출 - 파일명에서 경로 구분자 제거
    String sanitizedFilename = Paths.get(filename).getFileName().toString();
    if (sanitizedFilename.isBlank()) {
      throw new IllegalArgumentException("유효한 파일명이 없습니다");
    }
    
    String storedName = UUID.randomUUID() + "-" + sanitizedFilename;
    Path targetPath = rootPath.resolve("files").resolve(storedName); // 파일 경로 생성

    if (!targetPath.normalize().startsWith(rootPath.resolve("files"))) {
      throw new IllegalArgumentException("잘못된 파일 경로입니다");
    }

    try {
      Files.writeString( // 문자열을 파일에 쓴다
          targetPath,
          content,
          StandardCharsets.UTF_8, // 한글 깨짐 방지
          StandardOpenOption.CREATE, // 파일이 없으면 새로 생성
          StandardOpenOption.TRUNCATE_EXISTING, // 파일이 이미 있으면 내용을 비우고 덮어씀
          StandardOpenOption.WRITE // 쓰기 권한으로 열기
      );
    } catch (IOException e) {
      throw new IllegalStateException("정보 저장에 실패하였습니다", e);
    }

    // 파일 크기 계산
    long fileSize;
    try {
      fileSize = Files.size(targetPath); // 파일의 크기를 바이트 단위로 저장
    } catch (IOException e) {
      fileSize = content.getBytes(StandardCharsets.UTF_8).length;
      // 파일 읽기에 실패하면, 메모리에 있는 문자열의 byte 길이를 대신 사용
    }

    StoredFile file = StoredFile.create(
        sanitizedFilename,
        storedName,
        contentType,
        fileSize,
        targetPath.toString()
    );

    try {
      return fileRepository.save(file);
    } catch (RuntimeException e) {
      try {
        Files.deleteIfExists(targetPath);
      } catch (IOException cleanupEx) {
        e.addSuppressed(cleanupEx);
      }
      throw e;
    }
  }

  // UPDATE
  // 파일은 수정하는 것이 아니라, 기존 파일을 삭제하고 새로운 파일을 만드는 것이 좋음

  // READ
  @Transactional(readOnly = true)
  public StoredFile getById(Long id) {
    return fileRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 파일이 존재하지 않습니다: " + id));
  }

  @Transactional(readOnly = true)
  public Resource getResource(Long id) {
    StoredFile file = getById(id); // StoredFile 조회
    FileSystemResource resource = new FileSystemResource(file.getStoragePath());
    // storagePath를 FileSystemResource로 래핑해서 컨트롤러에 반환

    if (!resource.exists() || !resource.isReadable()) {
      throw new NoSuchElementException("파일을 찾을 수 없거나 읽을 수 없습니다: " + file.getStoragePath());
    }

    return resource;
  }

  // DELETE
  @Transactional
  public void delete(StoredFile file) {
    if (file == null) {
      return;
    }

    Path path = Path.of(file.getStoragePath());
    try {
      boolean deleted = Files.deleteIfExists(path); // 파일이 있으면 삭제하고 true 반환
      if (!deleted) {
        throw new NoSuchElementException("삭제할 파일이 존재하지 않습니다: " + path);
      }
    } catch (IOException e) {
      throw new IllegalStateException("파일 삭제에 실패했습니다: " + path, e);
    }

    fileRepository.delete(file);
  }
}
