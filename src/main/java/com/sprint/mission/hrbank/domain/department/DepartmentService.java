package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;

  public DepartmentDto createDepartment(DepartmentCreateRequest request) {
    if (departmentRepository.existsByName(request.name())) {
      throw new RuntimeException("동일한 이름을 가진 부서가 있습니다");
    }

    Department department = departmentMapper.toEntity(request);
    departmentRepository.save(department);

    return departmentMapper.toDto(department, 0);
  }
}
