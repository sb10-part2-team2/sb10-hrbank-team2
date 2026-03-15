package com.sprint.mission.hrbank.domain.file.controller;

import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import com.sprint.mission.hrbank.domain.file.service.FileService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  // 파일 다운로드 - /api/files/{id}/download - 파일을 다운로드합니다.
  // 200 - 다운로드 성공
  // 404 - 파일을 찾을 수 없음
  // 500 - 서버 오류
  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
    StoredFile metaData = fileService.getById(id); // 파일 메타데이터 조회
    Resource resource = fileService.getResource(id); // 실제 파일 스트림을 Resource 형태로 조회

    // 한글 파일명 깨짐 문제 해결
    String encodedFilename = URLEncoder.encode(metaData.getOriginalName(), StandardCharsets.UTF_8)
        // 파일명을 UTF-8기준으로 안전한 ASCII 형태로 변환
        .replace("+", "%20");
    // URLEncoder는 공백을 +로 바꾸는데, HTTP 헤더의 파일명 파라미터에서는 %20이 더 표준적

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(metaData.getContentType()))
        // DB에 저장된 MIME 타입을 응답 Content-Type으로 설정
        .header(
            HttpHeaders.CONTENT_DISPOSITION, // 응답 헤더의 이름을 Content-Disposition으로 지정
            "attachment; filename*=UTF-8''" + encodedFilename
        )
        // attachment: 브라우저가 콘텐츠를 파일로 다운로드하도록 강제
        // filename = 저장될 기본 파일명을 지정
        .body(resource); // 응답 본문에 실제 파일 내용을 담아 전송
  }
}
