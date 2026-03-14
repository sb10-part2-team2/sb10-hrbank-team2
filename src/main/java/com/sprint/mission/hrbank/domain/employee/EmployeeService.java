package com.sprint.mission.hrbank.domain.employee;

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
@Transactional
public class EmployeeService {
  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;
  private final EmployeeMapper employeeMapper;


  public CursorPageResponseEmployeeDto getEmployees(EmployeeSearchRequest req) {
    Objects.requireNonNull(req, ("유효하지 않은 요청!"));

    // 기본값이 존재하는 필드들. 해당 필드들이 null일 경우 기본값으로 초기화합니다.
    int size = req.size() == 0 ? 10 : req.size(); // int는 null과 비교 불가 (리터럴) 그 대신 0
    String sortField = req.sortField() == null ? "name" : req.sortField();
    String sortDirection = req.sortDirection() == null ? "asc" : req.sortDirection();
    return // TODO: 추후 구현 예정
  }


  public EmployeeDto create(EmployeeCreateRequest req, MultipartFile profile) {
    Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

    Optional<Department> department = departmentRepository.findById(req.departmentId());

    if (department.isEmpty()) {
      throw new NoSuchElementException("해당 부서를 찾을 수 없음");
    }

    // String으로 들어온 입사일을 LocalDate로 전환함.
    LocalDate hireDate = LocalDate.parse(req.hireDate());

    File file = null;
    //TODO: 추후 FILE 부분 완성이 되면 구현 예정입니다.
     if (profile != null) {
//         file = new File();

    //  file 영속화
    //  fileRepository.save(file);
     }

    Employee employee = new Employee(
        req.name(),
        req.email(),
        department.get(),
        req.position(),
        hireDate,
        file
    );

    // 새로 생성된 유저 엔티티를 영속화함.
    Employee saved = employeeRepository.save(employee); // 레포지토리 인터페이스를 통해 영속화
      return employeeMapper.entityToDto(saved); // 그 후 employeeDto 형식으로 리턴.ㅎ
    }
}
