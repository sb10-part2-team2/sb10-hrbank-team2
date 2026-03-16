package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import com.sprint.mission.hrbank.domain.department.Department;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
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
  private Department department;

  @Column(nullable = false)
  private String position;

  @Column(nullable = false)
  private LocalDate hireDate;

  @Column(nullable = false)
  private EmployeeStatus status;

  // 추후 프로필 이미지 엔티티와 연동할 예정
  // Employee.java에서 StoredFile과의 잘못된 연관관계를 수정 (OneToMany -> OneToOne)
  @OneToOne(mappedBy = "employee")
  private StoredFile profileImage;

  Employee(String name, String email, Department department, String position, LocalDate hiredDate,
      StoredFile profileImage) {
    this.name = name;
    this.email = email;
    this.employeeNumber =
        "EMP-" + hiredDate + Instant.now().toEpochMilli(); // 자연생성 규칙 : 입사일-생성한시간정보
    this.department = department;
    this.position = position;
    this.hireDate = hiredDate;
    this.status = EmployeeStatus.ACTIVE;
    this.profileImage = profileImage;


  }


}
