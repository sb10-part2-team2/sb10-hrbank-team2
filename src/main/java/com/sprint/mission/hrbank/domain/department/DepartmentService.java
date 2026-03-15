package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentUpdateRequest;
import com.sprint.mission.hrbank.domain.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;
  private final EmployeeRepository employeeRepository;

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
    long employeeCount = employeeRepository.countAllByDepartmentId(departmentId);

    return departmentMapper.toDto(department, employeeCount);
  }

  public void deleteDepartment(Long departmentId) {
    if (!departmentRepository.existsById(departmentId)) {
      throw new RuntimeException("해당하는 부서가 없습니다");
    }

    // 조건부 삭제 쿼리
    int deletedCount = departmentRepository.deleteByIdIfNotEmployee(departmentId);

    if (deletedCount == 0) {
      throw new RuntimeException("부서에 소속된 직원이 있어서 삭제할 수 없습니다");
    }
  }

  void validateUniqueName(String name) {
    if (departmentRepository.existsByName(name)) {
      throw new RuntimeException("동일한 이름을 가진 부서가 있습니다");
    }
  }
}
