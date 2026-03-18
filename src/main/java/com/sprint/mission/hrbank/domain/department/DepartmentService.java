package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.department.dto.CursorPageResponseDepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentCreateRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentDto;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentSearchRequest;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentUpdateRequest;
import com.sprint.mission.hrbank.domain.employee.repository.EmployeeRepository;
import com.sprint.mission.hrbank.global.exception.CustomException;
import com.sprint.mission.hrbank.global.exception.ErrorCode;
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
        .orElseThrow(() -> new CustomException(ErrorCode.DEPARTMENT_NOT_FOUND));
    long employeeCount = employeeRepository.countAllByDepartmentId(departmentId);

    return departmentMapper.toDto(department, employeeCount);
  }

  @Transactional(readOnly = true)
  public CursorPageResponseDepartmentDto findAllDepartment(DepartmentSearchRequest request) {
    String normalizedCursor = Optional.ofNullable(request.cursor())
        .filter(s -> !s.isBlank())
        .orElse(null);

    if (request.size() < 1) {
      throw new CustomException(ErrorCode.CLIENT_ERROR, "페이지 크기가 1 이상이어야 합니다");
    }
    if ((normalizedCursor != null && request.idAfter() == null)
        || (normalizedCursor == null && request.idAfter() != null)) {
      throw new CustomException(ErrorCode.CLIENT_ERROR, "커서, id가 둘 다 있거나 없어야 합니다");
    }

    // 커서기능을 하기위한 요청 크기 + 1
    int sizeAddOne = request.size() + 1;

    // 검색 키워드
    String keyword = Optional.ofNullable(request.nameOrDescription())
        .filter(s -> !s.isBlank())
        .orElse(null);
    // 정렬 기준
    String sortField = request.sortField();
    if (!"name".equals(sortField) && !"establishedDate".equals(sortField)) {
      throw new CustomException(ErrorCode.CLIENT_ERROR, "지원하지 않는 정렬기준입니다");
    }
    // 정렬기준이 establishedDate면 LocalDate 타입으로 변환
    LocalDate cursorDate = Optional.ofNullable(normalizedCursor)
        .filter(s -> sortField.equals("establishedDate"))
        .map(LocalDate::parse)
        .orElse(null);

    // Pageable 생성을 위한 Sort 생성
    Sort sort = "ASC".equalsIgnoreCase(request.sortDirection())
        ? Sort.by(sortField).ascending()
        : Sort.by(sortField).descending();
    // Pageable 생성
    Pageable pageable = PageRequest.of(0, sizeAddOne, sort);

    // 실행되는 함수? 1차: 정렬방향, 2차: 정렬필드에 맞춰서 repository 실행
    // 결과값: (부서, 부서 인원수)
    List<Object[]> queryResults = switch (Sort.Direction.fromString(request.sortDirection())) {
      case ASC -> sortField.equals("establishedDate")
          ? departmentRepository.findByNameContainingOrDescriptionContainingOrderByEstablishedDateAsc(
          keyword, cursorDate, request.idAfter(), pageable)
          : departmentRepository.findByNameContainingOrDescriptionContainingOrderByNameAsc(
              keyword, normalizedCursor, request.idAfter(), pageable);
      case DESC -> sortField.equals("establishedDate")
          ? departmentRepository.findByNameContainingOrDescriptionContainingOrderByEstablishedDateDesc(
          keyword, cursorDate, request.idAfter(), pageable)
          : departmentRepository.findByNameContainingOrDescriptionContainingOrderByNameDesc(
              keyword, normalizedCursor, request.idAfter(), pageable);
    };

    int querySize = queryResults.size();
    boolean hasNext = querySize > request.size();

    // ex) 요청크기: 10에 hasNext가 true면, 쿼리크기 11
    // 요청크기 10의 마지막 리스트를 읽어야 하니까 get(9)이어야 함
    Object cursor = hasNext ? queryResults.get(request.size() - 1) : null;
    String nextCursor = null;
    Long nextIdAfter = null;

    if (hasNext && cursor instanceof Object[] c) {
      Object entity = c[0];
      // 쿼리+1 결과물 -> 쿼리
      queryResults = queryResults.subList(0, request.size());
      if (entity instanceof Department d) {
        // 정렬 기준에 따라 반환 마지막값을 커서값으로 동적 바인딩
        nextCursor = sortField.equals("establishedDate")
            ? d.getEstablishedDate().toString()
            : d.getName();
        // 커서로 저장한 객체 ID 동적 바인딩
        nextIdAfter = d.getId();
      }
    }

    // Object: (부서, 부서 인원수)가 Dto로 변환
    List<DepartmentDto> departmentDto = queryResults.stream()
        .map(r -> departmentMapper.toDto((Department) r[0], (long) r[1]))
        .toList();

    // 검색 키워드 기준 총 부서수
    long totalElements = keyword == null
        ? departmentRepository.count()
        : departmentRepository.countByNameOrDescriptionContaining(keyword);

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
        .orElseThrow(() -> new CustomException(ErrorCode.DEPARTMENT_NOT_FOUND));

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
      throw new CustomException(ErrorCode.DEPARTMENT_NOT_FOUND);
    }

    // 조건부 삭제 쿼리
    int deletedCount = departmentRepository.deleteByIdIfNotEmployee(departmentId);

    if (deletedCount == 0) {
      throw new CustomException(ErrorCode.DEPARTMENT_NOT_DELETABLE);
    }
  }

  void validateUniqueName(String name) {
    if (departmentRepository.existsByName(name)) {
      throw new CustomException(ErrorCode.DEPARTMENT_NAME_DUPLICATE);
    }
  }
}
