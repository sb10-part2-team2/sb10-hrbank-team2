package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.department.Department;
import com.sprint.mission.hrbank.domain.department.DepartmentRepository;
import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCountRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import com.sprint.mission.hrbank.domain.employee.mapper.EmployeeMapper;
import com.sprint.mission.hrbank.domain.employee.repository.EmployeeRepository;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;

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

  // 직원 상세 목록 조회 서비스 메서드
  @Transactional
  public EmployeeDto getDetail(long id) {
    Employee employee = employeeRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않음!"));

    return (employeeMapper.entityToDto(employee)); // EmployeeDto 변환 후 리턴.
  }

  // 직원 생성 서비스 메서드
  @Transactional
  public EmployeeDto create(@RequestPart EmployeeCreateRequest req,
      @RequestPart MultipartFile profile) {
    Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

    Optional<Department> department = departmentRepository.findById(req.departmentId());

    if (department.isEmpty()) {
      throw new NoSuchElementException("해당 부서를 찾을 수 없음");
    }
//
//    StoredFile file = null;
//    //TODO: 추후 FILE 부분 완성이 되면 구현 예정입니다.
//    if (profile != null) {
//      file = new StoredFile();
//
//      //  file 영속화
//      //  fileRepository.save(file);
//    }

    Employee employee = new Employee(
        req.name(),
        req.email(),
        department.get(),
        req.position(),
        req.hireDate(),
        null
    );

    // 새로 생성된 유저 엔티티를 영속화함.
    Employee saved = employeeRepository.save(employee); // 레포지토리 인터페이스를 통해 영속화
    return employeeMapper.entityToDto(saved); // 그 후 employeeDto 형식으로 리턴.ㅎ
  }

  // 삭제를 수행하는 서비스 계층 메소드
  @Transactional
  public void delete(Long id) {
    Objects.requireNonNull(id, "유효하지 않은 id입니다!");
    if (!employeeRepository.existsById(id)) {
      throw new NoSuchElementException("해당 유저를 찾을 수 없음!");
    }
    employeeRepository.deleteById(id); //JPARepository에 기본 내장된 deleteById 수행.
  }

}
