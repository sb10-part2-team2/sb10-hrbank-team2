package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.CursorPageResponseDepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentSearchRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentUpdateRequest;
import com.sprint.mission.hrbank.domain.employee.repository.EmployeeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  @Transactional(readOnly = true)
  public DepartmentDto findDepartment(long departmentId) {
    Department department = departmentRepository.findById(departmentId)
        .orElseThrow(() -> new RuntimeException("해당하는 부서가 없습니다"));
    long employeeCount = employeeRepository.countAllByDepartmentId(departmentId);

    return departmentMapper.toDto(department, employeeCount);
  }

  @Transactional(readOnly = true)
  public CursorPageResponseDepartmentDto findAllDepartment(DepartmentSearchRequest request) {
    int sizeAddOne = request.size() + 1;
    String sortField = request.sortField();
    LocalDate cursorDate = Optional.ofNullable(request.cursor())
        .filter(s -> !s.isBlank() && sortField.equals("establishedDate"))
        .map(LocalDate::parse)
        .orElse(null);

    Sort sort = "ASC".equalsIgnoreCase(request.sortDirection())
        ? Sort.by(sortField).ascending()
        : Sort.by(sortField).descending();
    Pageable pageable = PageRequest.of(0, sizeAddOne, sort);

    List<Object[]> queryResults = switch (Sort.Direction.fromString(request.sortDirection())) {
      case ASC -> sortField.equals("establishedDate")
          ? departmentRepository.findByNameContainingOrDescriptionContainingOrderByEstablishedDateAsc(
          request.nameOrDescription(), cursorDate, request.idAfter(), pageable)
          : departmentRepository.findByNameContainingOrDescriptionContainingOrderByNameAsc(
              request.nameOrDescription(), request.cursor(), request.idAfter(), pageable);
      case DESC -> sortField.equals("establishedDate")
          ? departmentRepository.findByNameContainingOrDescriptionContainingOrderByEstablishedDateDesc(
          request.nameOrDescription(), cursorDate, request.idAfter(), pageable)
          : departmentRepository.findByNameContainingOrDescriptionContainingOrderByNameDesc(
              request.nameOrDescription(), request.cursor(), request.idAfter(), pageable);
    };
    int querySize = queryResults.size();
    boolean hasNext = querySize > request.size();
    Object cursor = hasNext ? queryResults.get(request.size() - 1) : null;
    String nextCursor = null;
    Long nextIdAfter = null;
    if (hasNext && cursor instanceof Object[] c) {
      Object entity = c[0];
      queryResults = queryResults.subList(0, request.size());
      if (entity instanceof Department d) {
        nextCursor = sortField.equals("establishedDate")
            ? d.getEstablishedDate().toString()
            : d.getName();
        nextIdAfter = d.getId();
      }
    }

    List<DepartmentDto> departmentDto = queryResults.stream()
        .map(r -> departmentMapper.toDto((Department) r[0], (long) r[1]))
        .toList();
    long totalElements = departmentRepository.count();

    return new CursorPageResponseDepartmentDto(
        departmentDto,
        nextCursor,
        nextIdAfter,
        departmentDto.size(),
        totalElements,
        hasNext
    );
  }

  public DepartmentDto updateDepartment(long departmentId, DepartmentUpdateRequest request) {
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
