package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.CursorPageResponseDepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentSearchRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping
  public ResponseEntity<DepartmentDto> createDepartment(
      @Valid @RequestBody DepartmentCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(departmentService.createDepartment(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<DepartmentDto> findDepartment(
      @PathVariable("id") long departmentId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(departmentService.findDepartment(departmentId));
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseDepartmentDto> findAllDepartment(
      @ModelAttribute DepartmentSearchRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(departmentService.findAllDepartment(request));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<DepartmentDto> updateDepartment(
      @PathVariable("id") Long departmentId,
      @Valid @RequestBody DepartmentUpdateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(departmentService.updateDepartment(departmentId, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteDepartment(
      @PathVariable("id") Long departmentId) {
    departmentService.deleteDepartment(departmentId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
