package com.sprint.mission.hrbank.domain.changelog;

import com.sprint.mission.hrbank.domain.employee.Employee;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;
  private final ChangeLogMapper changeLogMapper;

  // 직원 추가/수정/삭제 시 호출
  public void createChangeLog(Employee before, Employee after, ChangeLogType type, String ipAddress,
      String memo) {
    if (before == null && after == null) {
      throw new IllegalArgumentException("before와 after 중 하나는 반드시 필요합니다.");
    }
    Employee target = after != null ? after : before;

    // Null Safe하게 프로필 이미지 ID 추출
    Long profileImageId = null;
    if (target.getProfileImage() != null && !target.getProfileImage().isEmpty()) {
      // OneToMany이므로 리스트에서 하나를 가져오거나 대표 이미지를 로직에 맞게 선택
      // 리스트의 첫 번째 이미지를 스냅샷 ID로 선택
      profileImageId = target.getProfileImage().get(0).getId();
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

    // 정렬 방향과 기준 설정 (기본값 : createdAt 내림차순)
    Sort sort = Sort.by(
        request.sortDirection()
            .equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC, "createdAt");

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
}
