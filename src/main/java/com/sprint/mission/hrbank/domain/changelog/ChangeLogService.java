package com.sprint.mission.hrbank.domain.changelog;

import com.sprint.mission.hrbank.domain.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeLogService {

  private final ChangeLogRepository changeLogRepository;

  // 직원 추가/수정/삭제 시 호출
  public void createChangeLog(Employee before, Employee after, ChangeLogType type, String ipAddress,
      String memo) {
    Employee target = after != null ? after : before;

    // Null Safe하게 프로필 이미지 ID 추출
    Long profileImageId = null;
    if (target.getProfileImage() != null && !target.getProfileImage().isEmpty()) {
      // OneToMany이므로 리스트에서 하나를 가져오거나 대표 이미지를 로직에 맞게 선택
      // profileImageId =
    }

    // ChangeLog 생성
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
    if (before.getDepartment() != null && after.getDepartment() != null
        && !before.getDepartment().getId().equals(after.getDepartment().getId())) {
      changeLog.addDiff("department", before.getDepartment().getName(),
          after.getDepartment().getName());
    }

    // 직함 수정 시
    if (!before.getPosition().equals(after.getPosition())) {
      changeLog.addDiff("position", before.getPosition(), after.getPosition());
    }

    // 입사일 수정 시
    if (!before.getHireDate().equals(after.getHireDate())) {
      changeLog.addDiff("hiredDate", before.getHireDate().toString(),
          after.getHireDate().toString());
    }

    // 상태 변경 시
    if (before.getStatus() != (after.getStatus())) {
      changeLog.addDiff("status", before.getStatus().name(), after.getStatus().name());
    }

  }
}
