package com.sprint.mission.hrbank.domain.changelog;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@Table(name = "CHANGE_LOG_DIFFS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLogDiff extends BaseEntity {

  // 수정된 이력 ID
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "change_log_id", nullable = false)
  private ChangeLog changeLog;

  // 수정된 속성 정보
  @Column(nullable = false)
  private String propertyName;

  // 수정 전 값
  @Column(columnDefinition = "TEXT") // 어떤 필드 값이든 들어올 수 있어서 길이 예측이 안 되기 때문에 TEXT로 설정
  private String beforeValue;

  // 수정 후 값
  @Column(columnDefinition = "TEXT")
  private String afterValue;

  @Builder
  public ChangeLogDiff(ChangeLog changeLog, String propertyName, String beforeValue,
      String afterValue) {
    this.changeLog = changeLog;
    this.propertyName = propertyName;
    this.beforeValue = beforeValue;
    this.afterValue = afterValue;
  }
}
