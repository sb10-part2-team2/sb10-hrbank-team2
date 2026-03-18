package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.changelog.IpUtil;
import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCountRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDistributionDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendInterval;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PatchMapping;
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

  @GetMapping("/stats/distribution")
  @Operation(summary = "직원 분포 조회", description = "지정된 기준으로 그룹화된 직원 분포를 조회합니다.")
  public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
      @Parameter(description = "그룹화 기준 (department: 부서별, position: 직무별, 기본값: department)")
      @RequestParam(defaultValue = "department") String groupBy,
      @Parameter(description = "직원 상태 (재직중, 휴직중, 퇴사, 기본값: ACTIVE)")
      @RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
  ) {
    return ResponseEntity.ok(employeeService.getEmployeeDistribution(groupBy, status));
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

  // 직원 수정 엔드포인트
  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeDto> updateEmployee(
      @PathVariable Long id, // 수정하고자 하는 Employee의 id

      @Valid @RequestPart EmployeeUpdateRequest req, // 직원 정보 수정 dto
      @RequestPart(required = false) MultipartFile profile, // (선택적) 프로필 이미지
      HttpServletRequest request // ip 주소를 추출하기 위해 HttpServletRequest를 매개변수로 받음
  ) {

    // IP 유틸 함수를 통해 HttpServletRequest에서 IP를 추출함.
    String clientIp = IpUtil.getClientIp(request);

    // 서비스 계층의 update 메서드 실행
    return ResponseEntity.ok(employeeService.update(id, req, profile, clientIp));

  }


  // 직원 상세 조회 엔드포인트
  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> getEmployeeDetail(@PathVariable Long id) {
    return ResponseEntity.ok(employeeService.getDetail(id));
  }

  // 직원 생성 엔드포인트
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeDto> createEmployee(
      @Valid @RequestPart EmployeeCreateRequest req,
      @RequestPart(required = false) MultipartFile profile,
      HttpServletRequest request) {
    String clientIp = IpUtil.getClientIp(request);
    return ResponseEntity.ok(employeeService.create(req, profile, clientIp));
  }

  // id를 Path Variable로 받고 삭제 수행
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(
      @PathVariable Long id,
      HttpServletRequest request) {
    String clientIp = IpUtil.getClientIp(request);
    employeeService.delete(id, clientIp);
    return ResponseEntity.noContent().build();
  }
}
