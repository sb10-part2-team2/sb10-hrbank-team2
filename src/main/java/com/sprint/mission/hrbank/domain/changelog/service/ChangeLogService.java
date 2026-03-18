package com.sprint.mission.hrbank.domain.changelog.service;

import com.sprint.mission.hrbank.domain.changelog.ChangeLog;
import com.sprint.mission.hrbank.domain.changelog.ChangeLogType;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogDetailDto;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogDto;
import com.sprint.mission.hrbank.domain.changelog.dto.ChangeLogSearchRequest;
import com.sprint.mission.hrbank.domain.changelog.dto.CursorPageResponseChangeLogDto;
import com.sprint.mission.hrbank.domain.changelog.mapper.ChangeLogMapper;
import com.sprint.mission.hrbank.domain.changelog.repository.ChangeLogRepository;
import com.sprint.mission.hrbank.domain.employee.Employee;
import com.sprint.mission.hrbank.global.exception.CustomException;
import com.sprint.mission.hrbank.global.exception.ErrorCode;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeLogMapper changeLogMapper;

  // 직원 추가/수정/삭제 시 호출
  @Transactional
  public void createChangeLog(Employee before, Employee after, ChangeLogType type, String ipAddress,
      String memo) {
// 1. 파라미터 검증 (400 Bad Request 성격)
    if (before == null && after == null) {
      throw new CustomException(ErrorCode.CHANGE_LOG_BEFORE_AFTER_REQUIRED);
    }

    // 수정(UPDATED)일 때는 반드시 전/후 데이터가 모두 있어야 함
    if (type == ChangeLogType.UPDATED && (before == null || after == null)) {
      throw new CustomException(ErrorCode.CHANGE_LOG_UPDATED_REQUIRES_BOTH);
    }

    Employee target = after != null ? after : before;

    // Null Safe하게 프로필 이미지 ID 추출
    Long profileImageId = null;
    if (target.getProfileImage() != null) {
      profileImageId = target.getProfileImage().getId();
    }

    // ChangeLog 생성(Builder 패턴 사용)
    ChangeLog changeLog = ChangeLog.builder()
        .employeeId(target.getId())
        .type(type)
        .employeeNumberSnapshot(target.getEmployeeNumber())
        .memo(memo)
        .ipAddress(ipAddress)
        .employeeNameSnapshot(target.getName())
        .profileImageIdSnapshot(profileImageId)
        .build();

    // 수정일 때 diff 저장
    if (type == ChangeLogType.UPDATED && before != null && after != null) {
      compareAndAddDiffs(changeLog, before, after);
    }

    changeLogRepository.save(changeLog);

  }

  private void compareAndAddDiffs(ChangeLog changeLog, Employee before, Employee after) {

    // 이름 수정 시
    if (!before.getName().equals(after.getName())) {
      changeLog.addDiff("name", before.getName(), after.getName());
    }

    // 이메일 수정 시
    if (!before.getEmail().equals(after.getEmail())) {
      changeLog.addDiff("email", before.getEmail(), after.getEmail());
    }

    // 부서 수정 시
    Long beforeDepartmentId =
        before.getDepartment() != null ? before.getDepartment().getId() : null;
    Long afterDepartmentId =
        after.getDepartment() != null ? after.getDepartment().getId() : null;
    if (!java.util.Objects.equals(beforeDepartmentId, afterDepartmentId)) {
      String beforeDepartmentName =
          before.getDepartment() != null ? before.getDepartment().getName() : null;
      String afterDepartmentName =
          after.getDepartment() != null ? after.getDepartment().getName() : null;
      changeLog.addDiff("department", beforeDepartmentName, afterDepartmentName);
    }

    // 직함 수정 시
    if (!before.getPosition().equals(after.getPosition())) {
      changeLog.addDiff("position", before.getPosition(), after.getPosition());
    }

    // 입사일 수정 시
    if (!before.getHireDate().equals(after.getHireDate())) {
      changeLog.addDiff("hireDate", before.getHireDate().toString(),
          after.getHireDate().toString());
    }

    // 상태 변경 시
    if (before.getStatus() != (after.getStatus())) {
      changeLog.addDiff("status", before.getStatus().name(), after.getStatus().name());
    }

  }


  // 목록 조회
  public CursorPageResponseChangeLogDto getChangeLogs(ChangeLogSearchRequest request) {

    String sortProperty = request.sortField().equals("ipAddress") ? "ipAddress" : "createdAt";

    // 정렬 방향과 기준 설정 (기본값 : createdAt 내림차순)
    Sort sort = Sort.by(
        request.sortDirection()
            .equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC, sortProperty);

    // 페이지 크기와 정렬 조건으로 Pageable 생성
    Pageable pageable = PageRequest.of(0, request.size(), sort);

    // 검색 조건과 커서(idAfter) 기반으로 이력 목록 조회
    Slice<ChangeLog> changeLogSlices = changeLogRepository.searchChangeLogs(
        request.employeeNumber(),
        request.type(), request.memo(),
        request.ipAddress(), request.atFrom(), request.atTo(), request.idAfter(),
        pageable);

    // Entity -> DTO 변환
    List<ChangeLogDto> content = changeLogSlices.getContent().stream()
        .map(changeLogMapper::toDto)
        .toList();

    // 검색 조건 기준 전체 건수 조회 (totalElements용, idAfter 제외)
    long totalElements = changeLogRepository.countByConditions(request.employeeNumber(),
        request.type(),
        request.memo(),
        request.ipAddress(), request.atFrom(), request.atTo());

    // 다음 페이지가 있으면 마지막 요소의 id를 nextIdAfter로 설정
    Long nextIdAfter = (changeLogSlices.hasNext() && !content.isEmpty())
        ? content.get(content.size() - 1).id()
        : null;

    // 다음 페이지가 있으면 마지막 요소의 at을 nextCursor로 설정
    Instant nextCursor = nextIdAfter != null
        ? content.get(content.size() - 1).at()
        : null;

    return new CursorPageResponseChangeLogDto(
        content, nextCursor, nextIdAfter, pageable.getPageSize(), totalElements,
        changeLogSlices.hasNext()
    );
  }

  // 상세 목록 조회
  public ChangeLogDetailDto getChangeLogDetail(Long id) {
    ChangeLog detailLog = changeLogRepository.findDetailById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.CHANGE_LOG_NOT_FOUND));
    return changeLogMapper.toDetailDto(detailLog);
  }

  // 수정 이력 건수 조회
  public Long getChangeLogCount(Instant fromDate, Instant toDate) {
    // 5. 날짜 범위 검증 (시작일이 종료일보다 늦을 경우)
    if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
      throw new CustomException(ErrorCode.CHANGE_LOG_INVALID_DATE_RANGE);
    }

    return changeLogRepository.countChangeLogs(fromDate, toDate);
  }
}
