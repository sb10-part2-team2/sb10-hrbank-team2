package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;
//  private final EmployeeRepository employeeRepository;     추후 추가되면

  public DepartmentDto createDepartment(DepartmentCreateRequest request) {
    validateUniqueName(request.name());

    Department department = departmentMapper.toEntity(request);
    departmentRepository.save(department);

    return departmentMapper.toDto(department, 0);
  }

  public DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest request) {
    Department department = departmentRepository.findById(departmentId)
        .orElseThrow(() -> new RuntimeException("해당하는 부서가 없습니다"));

    if (request.name() != null &&
        !department.getName().equals(request.name())) {
      validateUniqueName(request.name());
    }
    
    department.updateFromDto(request);
    departmentRepository.save(department);
//    Long employeeCount = employeeRepository.findAllByDepartmentId(departmentId).size(); 추구 repository가 추가되면..

    return departmentMapper.toDto(department, -1);    // repository가 추가되면 교체예정
  }

  void validateUniqueName(String name) {
    if (departmentRepository.existsByName(name)) {
      throw new RuntimeException("동일한 이름을 가진 부서가 있습니다");
    }
  }
}
