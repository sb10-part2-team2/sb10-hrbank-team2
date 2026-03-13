package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController {

  private final DepartmentService departmentService;

  @PostMapping()
  public ResponseEntity<DepartmentDto> createDepartment(
      @RequestBody DepartmentCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(departmentService.createDepartment(request));
  }
}
