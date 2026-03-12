package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;

  public DepartmentResponse createDepartment(DepartmentCreateRequest request) {
    if (departmentRepository.existsByName(request.getName())) {
      throw new RuntimeException("동일한 이름을 가진 부서가 있습니다");
    }

    Department department = new Department(
        request.getName(),
        request.getDescription(),
        request.getEstablishedDate());

    return DepartmentResponse.toDto(department, 0);
  }
}
