package com.sprint.mission.hrbank.domain.file.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * FileController - Swagger 문서
 */
@Tag(name = "파일 관리", description = "파일 관리 API")
public interface FileApi {

  /**
   * 파일 다운로드
   *
   */
  @Operation(summary = "파일 다운로드", description = "파일을 다운로드 합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "파일 다운로드 성공",
          content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
      ),
      @ApiResponse(
          responseCode = "404", description = "파일을 찾을 수 없음"
      )
  })
  @GetMapping("/{id}/download")
  ResponseEntity<Resource> download(@PathVariable("id") Long id);
}
