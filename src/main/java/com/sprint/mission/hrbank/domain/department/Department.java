package com.sprint.mission.hrbank.domain.department;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Department extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String establishedDate;   // ex: 2026-03-26(YYYY-MM-DD)

  public Department(String name, String description, String establishedDate) {
    this.name = name;
    this.description = description;
    this.establishedDate = establishedDate;
  }
}
