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
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeUpdateRequest;
import com.sprint.mission.hrbank.domain.employee.mapper.EmployeeMapper;
import com.sprint.mission.hrbank.domain.employee.repository.EmployeeRepository;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import com.sprint.mission.hrbank.domain.file.service.FileService;
import com.sprint.mission.hrbank.global.exception.CustomException;
import com.sprint.mission.hrbank.global.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
  private final ChangeLogService changeLogService;
  private final FileService fileService;

  // 직원 전체 목록 조회 서비스 메서드
  @Transactional
  public CursorPageResponseEmployeeDto getEmployees(EmployeeSearchRequest req) {
    Objects.requireNonNull(req, ("유효하지 않은 요청!"));

    return employeeRepository.search(req);
  }

  // 직원 수 계산 서비스 메서드
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

    Department department = departmentRepository.findById(req.departmentId())
        .orElseThrow(() -> new EntityNotFoundException("부서를 찾을 수 없습니다. ID: " + req.departmentId()));

    if (employeeRepository.existsByEmail(req.email())) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS, "중복된 이메일입니다.");
    }

    StoredFile file = null;
    if (profile != null) {
      file = fileService.saveData(profile);
    }

    Employee employee = new Employee(
        req.name(),
        req.email(),
        department,
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

  // 직원 정보 수정 서비스 메서드
  @Transactional
  public EmployeeDto update(Long id, EmployeeUpdateRequest req, MultipartFile profile, String ip) {
    Objects.requireNonNull(req, "유효하지 않은 요청입니다!");
    Objects.requireNonNull(ip, "유효하지 않은 IP입니다!");
    Objects.requireNonNull(id, "유효하지 않은 식별자입니다!");

    if (req.name() != null && req.name().isBlank()) {
      throw new IllegalArgumentException("이름은 공백일 수 없습니다.");
    }
    if (req.email() != null && req.email().isBlank()) {
      throw new IllegalArgumentException("이메일은 공백일 수 없습니다.");
    }
    if (req.position() != null && req.position().isBlank()) {
      throw new IllegalArgumentException("직함은 공백일 수 없습니다.");
    }

    // 수정하고자 하는 직원을 id로 찾음.
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않음"));

    // 수정 전 직원의 Snapshot을 저장함. (Department와 ProfileImage의 Id와 이름만 가지고 그 외 정보는 저장 X)
    Employee beforeSnapshot = employee.copyForSnapshot(); // 없으면 필요한 필드만 별도 변수로 저장

    // 요청에 이메일이 존재하면서 해당 이메일이 다른 사람의 이메일과 동일할 때 예외 던짐
    if (req.email() != null && employeeRepository.existsByEmailAndIdNot(req.email(), id)) {
      throw new IllegalStateException("이메일이 중복됩니다");
    }

    // 엔티티 내부에서 파라미터를 받아 업데이트
    employee.update(req.name(), req.email(), req.position(), req.hireDate(), req.status());

    // 부서를 수정하고자 할 때 요청으로 들어온 부서 ID로 검증 및 수정
    if (req.departmentId() != null) {
      Department department = departmentRepository.findById(req.departmentId())
          .orElseThrow(() -> new NoSuchElementException("부서가 존재하지 않음"));
      employee.setDepartment(department);
    }

    // 프로필 이미지를 새로 수정할 때, fileService의 saveData 메소드 호출하여 생성 및 저장.
    if (profile != null && !profile.isEmpty()) {
      employee.setProfileImage(fileService.saveData(profile));
    }

    // 변경 이력 서비스의 변경 이력 생성 메소드 호출 (해당 메소드 내에서 변경 사항(diff) 생성?)
    changeLogService.createChangeLog(beforeSnapshot, employee, ChangeLogType.UPDATED, ip,
        req.memo());

    // 변경 된 직원의 정보를 EmployeeDto 형식으로 반환.
    return employeeMapper.entityToDto(employee);
  }


  // 직원 정보 삭제 서비스 메소드
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
