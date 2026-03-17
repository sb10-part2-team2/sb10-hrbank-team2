package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService; // 추후 구현 예정

  // 직원 전체 목록 조회 엔드포인트
  @GetMapping
  public ResponseEntity<CursorPageResponseEmployeeDto> getEmployees(EmployeeSearchRequest req) {
    CursorPageResponseEmployeeDto response = employeeService.getEmployees(req);
    return ResponseEntity.ok(response);
  }

  // 직원 상세 조회 엔드포인트
  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> getEmployeeDetail(@PathVariable Long id) {
    return ResponseEntity.ok(employeeService.getDetail(id));
  }

  // 직원 생성 엔드포인트
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeDto> createEmployee(@RequestPart EmployeeCreateRequest req,
      @RequestPart(required = false) MultipartFile profile) {
    return ResponseEntity.ok(employeeService.create(req, profile));
  }

  // id를 Path Variable로 받고 삭제 수행
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
    employeeService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
