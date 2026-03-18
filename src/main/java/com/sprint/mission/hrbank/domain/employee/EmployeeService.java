package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.changelog.ChangeLogType;
import com.sprint.mission.hrbank.domain.changelog.service.ChangeLogService;
import com.sprint.mission.hrbank.domain.department.Department;
import com.sprint.mission.hrbank.domain.department.DepartmentRepository;
import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCountRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDistributionDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendInterval;
import com.sprint.mission.hrbank.domain.employee.mapper.EmployeeMapper;
import com.sprint.mission.hrbank.domain.employee.repository.EmployeeRepository;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import com.sprint.mission.hrbank.domain.file.service.FileService;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;
  private final FileService fileService;
  private final ChangeLogService changeLogService;

  // 직원 전체 목록 조회 서비스 메서드
  @Transactional
  public CursorPageResponseEmployeeDto getEmployees(EmployeeSearchRequest req) {
    Objects.requireNonNull(req, ("유효하지 않은 요청!"));
    return employeeRepository.search(req);
  }

  public long getEmployeeCount(EmployeeCountRequest req) {
    // 리포지토리에서 status가 null이면 기본적으로 재직자(ACTIVE, ON_LEAVE)를 카운트하도록 구현됨
    return employeeRepository.countEmployees(req);
  }

  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
      EmployeeStatus status) {
    return employeeRepository.getEmployeeDistribution(groupBy, status);
  }

  public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to,
      EmployeeTrendInterval interval) {
    // toDate가 없으면 현재 일시
    if (to == null) {
      to = LocalDate.now();
    }
    // fromDate가 없으면 interval에 따라 자동 계산
    if (from == null) {
      from = calculateFromDate(to, interval);
    }

    // 리포지토리에서 시계열 데이터(0 채우기 포함) 조회
    return employeeRepository.getEmployeeTrend(from, to, interval);
  }

  private LocalDate calculateFromDate(LocalDate to, EmployeeTrendInterval interval) {
    return switch (interval) {
      case DAILY -> to.minusDays(12);
      case WEEKLY -> to.minusWeeks(12);
      case MONTHLY -> to.minusMonths(12);
      case QUARTERLY -> to.minusMonths(36); // 12분기 전
      case YEARLY -> to.minusYears(12);
    };
  }

  // 직원 상세 목록 조회 서비스 메서드
  @Transactional
  public EmployeeDto getDetail(long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않음!"));

    return (employeeMapper.entityToDto(employee)); // EmployeeDto 변환 후 리턴.
  }

  // 직원 생성 서비스 메서드
  @Transactional
  public EmployeeDto create(
      EmployeeCreateRequest req,
      MultipartFile profile,
      String clientIp) {
    Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

    Optional<Department> department = departmentRepository.findById(req.departmentId());

    if (department.isEmpty()) {
      throw new NoSuchElementException("해당 부서를 찾을 수 없음");
    }

    StoredFile file = null;
    if (profile != null) {
      file = fileService.saveData(profile);
    }

    Employee employee = new Employee(
        req.name(),
        req.email(),
        department.get(),
        req.position(),
        req.hireDate(),
        file
    );

    // 새로 생성된 유저 엔티티를 영속화함.
    Employee saved = employeeRepository.save(employee); // 레포지토리 인터페이스를 통해 영속화

    // 수정 이력을 남김
    changeLogService.createChangeLog(null, saved, ChangeLogType.CREATED, clientIp, req.memo());

    return employeeMapper.entityToDto(saved); // 그 후 employeeDto 형식으로 리턴.
  }

  // 삭제를 수행하는 서비스 계층 메소드
  @Transactional
  public void delete(Long id, String clientIp) {
    Objects.requireNonNull(id, "유효하지 않은 id입니다!");

    // 유저 존재 여부 검증
    Employee target = employeeRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않음!"));

    // 수정 이력을 남김
    changeLogService.createChangeLog(target, null, ChangeLogType.DELETED, clientIp, "직원 삭제");

    employeeRepository.deleteById(id); //JPA Repository에 기본 내장된 deleteById 수행.

  }
}
