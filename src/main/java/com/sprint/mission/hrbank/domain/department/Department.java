package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.baseentity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Department extends BaseUpdatableEntity {

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Instant establishedDate;   // ex: 2026-03-26(YYYY-MM-DD). 포맷 형식은: ISO_LOCAL_DATE

  public Department(String name, String description, Instant establishedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = establishedDate;
  }
}
