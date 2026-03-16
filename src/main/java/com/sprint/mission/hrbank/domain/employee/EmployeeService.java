package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.department.Department;
import com.sprint.mission.hrbank.domain.department.DepartmentRepository;
import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import com.sprint.mission.hrbank.domain.employee.repository.EmployeeRepository;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import com.sprint.mission.hrbank.domain.employee.mapper.EmployeeMapper;
import java.time.LocalDate;
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

  public CursorPageResponseEmployeeDto getEmployees(EmployeeSearchRequest req) {
    Objects.requireNonNull(req, ("유효하지 않은 요청!"));

    return employeeRepository.search(req);
  }


  @Transactional
  public EmployeeDto create(EmployeeCreateRequest req, MultipartFile profile) {
    Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

    Optional<Department> department = departmentRepository.findById(req.departmentId());

    if (department.isEmpty()) {
      throw new NoSuchElementException("해당 부서를 찾을 수 없음");
    }

    SortedFile file = null;
    //TODO: 추후 FILE 부분 완성이 되면 구현 예정입니다.
    if (profile != null) {
//         file = new File();

      //  file 영속화
      //  fileRepository.save(file);
    }

    Employee employee = new Employee(
        req.name(),
        req.email(),
        null,
        req.position(),
        hireDate,
        null
    );

    // 새로 생성된 유저 엔티티를 영속화함.
    Employee saved = employeeRepository.save(employee); // 레포지토리 인터페이스를 통해 영속화
    return employeeMapper.entityToDto(saved); // 그 후 employeeDto 형식으로 리턴.ㅎ
  }
}
