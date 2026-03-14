package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.baseentity.BaseUpdatableEntity;
import com.sprint.mission.hrbank.domain.department.dto.DepartmentUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor                                   // MapStruct 활용 생성자
@NoArgsConstructor(access = AccessLevel.PROTECTED)    // JPA 필수 생성자
public class Department extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private LocalDate establishedDate;   // ex: 2026-03-26(YYYY-MM-DD). 포맷 형식은: ISO_LOCAL_DATE

  public void updateFromDto(DepartmentUpdateRequest req) {
    if (req.name() != null &&
        !this.name.equals(req.name())) {
      this.name = req.name();
    }
    if (req.description() != null &&
        !this.description.equals(req.description())) {
      this.description = req.description();
    }
    if (req.establishedDate() != null &&
        !this.establishedDate.equals(req.establishedDate())) {
      this.establishedDate = req.establishedDate();
    }
  }
}
