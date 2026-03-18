package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.baseentity.BaseUpdatableEntity;
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
public class Employee extends BaseUpdatableEntity {

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


  // 직원 정보 수정 시 차이를 나타내기 위해 수정 전 직원을 스냅샷으로 찍음 (얕은 복사)
  // 해당 메소드로 만들어진 Employee/Department/File은 Id와 이름만 존재하는 불완전한 엔티티
  // 영속화는 절대로 하지 말고 비교할 때에만 쓸 것.
  public Employee copyForSnapshot() {
    Employee snapshot = new Employee();
    snapshot.setId(this.getId());
    snapshot.setEmployeeNumber(this.employeeNumber);
    snapshot.setName(this.name);
    snapshot.setEmail(this.email);
    snapshot.setPosition(this.position);
    snapshot.setHireDate(this.hireDate);
    snapshot.setStatus(this.status);
    snapshot.setCreatedAt(this.getCreatedAt());
    snapshot.setUpdatedAt(this.getUpdatedAt());

    // 아이디와 이름만 가져옴. 부서를 통째로 가져오면 성능 저하 예측됨.
    if (this.department != null) {
      Department d = Department.builder()
          .name(this.department.getName())
          .build();
      d.setId(this.department.getId());
      snapshot.setDepartment(d);
    }

    // 아이디와 이름만 가져옴. 파일을 통째로 가져오면 성능 저하 예측됨.
    if (this.profileImage != null) {
      StoredFile f = StoredFile.builder()
          .originalName(this.profileImage.getOriginalName())
          .build();
      f.setId(this.profileImage.getId());
      snapshot.setProfileImage(f);
    }

    return snapshot;
  }

  public void update(String name, String email, String position, LocalDate hireDate,
      EmployeeStatus status) {
    if (name != null) {
      this.setName(name);
    }
    if (email != null) {
      this.setEmail(email);
    }
    if (position != null) {
      this.setPosition(position);
    }
    if (hireDate != null) {
      this.setHireDate(hireDate);
    }
    if (status != null) {
      this.setStatus(status);
    }
    this.setUpdatedAt(Instant.now());
  }
}
