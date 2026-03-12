package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Employee extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, updatable = false)
  private String employeeNumber;

  @ManyToOne
  @JoinColumn(name = "department_id")
  // 추후 부서 엔티티와 연동
  private Department department;

  @Column(nullable = false)
  private String position;

  @Column(nullable = false)
  private Instant hiredDate;

  @Column(nullable = false)
  private EmployeeStatus status;

  // 추후 프로필 이미지 엔티티와 연동할 예정
  @OneToMany(mappedBy = "employee")
  private BinaryContent profileImage;


}
