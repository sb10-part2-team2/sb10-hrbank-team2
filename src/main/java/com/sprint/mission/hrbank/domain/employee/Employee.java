package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import com.sprint.mission.hrbank.domain.department.Department;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "employees")
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

  @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "files_id")
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
