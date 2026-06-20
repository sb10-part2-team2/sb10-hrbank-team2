package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.CursorPageResponseDepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentSearchRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentUpdateRequest;
import com.sprint.mission.hrbank.global.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
@Tag(name = "부서 관리", description = "부서 관리 API")
public class DepartmentController {

  private final DepartmentService departmentService;

  @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")
  @ApiResponse(responseCode = "200", description = "등록 성공")
  @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이름", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @PostMapping
  public ResponseEntity<DepartmentDto> createDepartment(
      @Valid @RequestBody DepartmentCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(departmentService.createDepartment(request));
  }

  @Operation(summary = "부서 상세 조회", description = "부서 상세 정보를 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDto> findDepartment(
      @PathVariable("id") long departmentId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(departmentService.findDepartment(departmentId));
  }

  @Operation(summary = "부서 목록 조회", description = "부서 목록을 조회합니다.")
  @ApiResponse(responseCode = "200", description = "조회 성공")
  @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping
  public ResponseEntity<CursorPageResponseDepartmentDto> findAllDepartment(
      @ParameterObject @ModelAttribute DepartmentSearchRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(departmentService.findAllDepartment(request));
  }

  @Operation(summary = "부서 수정", description = "부서 정보를 수정합니다.")
  @ApiResponse(responseCode = "200", description = "수정 성공")
  @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이름", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentDto> updateDepartment(
      @PathVariable("id") Long departmentId,
      @Valid @RequestBody DepartmentUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(departmentService.updateDepartment(departmentId, request));
  }

  @Operation(summary = "부서 삭제", description = "부서를 삭제합니다.")
  @ApiResponse(responseCode = "204", description = "삭제 성공")
  @ApiResponse(responseCode = "400", description = "소속 직원이 있는 부서는 삭제할 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDepartment(
      @PathVariable("id") Long departmentId) {
    departmentService.deleteDepartment(departmentId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
