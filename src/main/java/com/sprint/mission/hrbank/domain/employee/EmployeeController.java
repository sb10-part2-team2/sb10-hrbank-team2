package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCountRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendInterval;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/employees")
@RequiredArgsConstructor
@Tag(name = "직원 관리", description = "직원 관리 API")
public class EmployeeController {

  private final EmployeeService employeeService; // 추후 구현 예정

  @GetMapping("/count")
  @Operation(summary = "직원 수 조회", description = "지정된 조건에 맞는 직원 수를 조회합니다. 상태 필터링 및 입사일 기간 필터링이 가능합니다.")
  public ResponseEntity<Long> getEmployeeCount(@ModelAttribute EmployeeCountRequest request) {
    return ResponseEntity.ok(employeeService.getEmployeeCount(request));
  }

  @GetMapping("/stats/trend")
  @Operation(summary = "직원 수 추이 조회", description = "지정된 기간 및 시간 단위로 그룹화된 직원 수 추이를 조회합니다. 파라미터를 제공하지 않으면 최근 12개월 데이터를 월 단위로 반환합니다.")
  public ResponseEntity<List<EmployeeTrendDto>> getTrend(
      @Parameter(description = "시작 일시 (기본값: 현재로부터 unit 기준 12개 이전)")
      @RequestParam(required = false) String from,
      @Parameter(description = "종료 일시 (기본값: 현재)")
      @RequestParam(required = false) String to,
      @Parameter(description = "시간 단위 (day, week, month, quarter, year, 기본값: month)")
      @RequestParam(defaultValue = "month") String unit
  ) {
    LocalDate fromDate;
    LocalDate toDate;

    try {
      fromDate = from != null ? LocalDate.parse(from) : null;
      toDate = to != null ? LocalDate.parse(to) : null;
    } catch (DateTimeParseException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 명세에 명시된 소문자 값들을 Enum으로 매핑
    EmployeeTrendInterval interval = switch (unit.toLowerCase()) {
      case "day" -> EmployeeTrendInterval.DAILY;
      case "week" -> EmployeeTrendInterval.WEEKLY;
      case "month" -> EmployeeTrendInterval.MONTHLY;
      case "quarter" -> EmployeeTrendInterval.QUARTERLY;
      case "year" -> EmployeeTrendInterval.YEARLY;
      default -> EmployeeTrendInterval.MONTHLY;
    };

    return ResponseEntity.ok(employeeService.getEmployeeTrend(fromDate, toDate, interval));
  }

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
