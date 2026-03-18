package com.sprint.mission.hrbank.domain.changelog;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "CHANGE_LOGS")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 기본 생성자 쓸 수 없음
public class ChangeLog extends BaseEntity {

  // 대상 직원 ID
  @Column(nullable = false)
  private Long employeeId;

  // 유형(직원 추가, 정보 수정, 직원 삭제)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChangeLogType type;

  // 대상 직원 사번
  @Column(nullable = false, length = 50)
  private String employeeNumberSnapshot;

  // 변경 상세 내용
  @OneToMany(mappedBy = "changeLog", cascade = CascadeType.PERSIST)
  private List<ChangeLogDiff> changeLogDiffs;

  // 메모(선택값)
  @Column(length = 500)
  private String memo;

  // 요청 IP 주소
  @Column(nullable = false, length = 45)
  private String ipAddress;

  // [추가] 조회 시 필요한 최소한의 정보를 스냅샷으로 들고 있음
  @Column(nullable = false)
  private String employeeNameSnapshot;

  private Long profileImageIdSnapshot;

  @Builder
  public ChangeLog(Long employeeId, ChangeLogType type, String employeeNumberSnapshot, String memo,
      String ipAddress, String employeeNameSnapshot, Long profileImageIdSnapshot) {
    this.employeeId = employeeId;
    this.type = type;
    this.employeeNumberSnapshot = employeeNumberSnapshot;
    this.memo = memo;
    this.ipAddress = ipAddress;
    this.changeLogDiffs = new ArrayList<>();
    this.employeeNameSnapshot = employeeNameSnapshot;
    this.profileImageIdSnapshot = profileImageIdSnapshot;
  }

  public void addDiff(String propertyName, String beforeValue, String afterValue) {
    ChangeLogDiff diff = ChangeLogDiff.builder()
        .changeLog(this)
        .propertyName(propertyName)
        .beforeValue(beforeValue)
        .afterValue(afterValue)
        .build();

    this.changeLogDiffs.add(diff);
  }
}
