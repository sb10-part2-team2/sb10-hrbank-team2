package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private Instant hiredDate;

    @Column(nullable = false)
    private EmployeeStatus status;

    // 추후 프로필 이미지 엔티티와 연동할 예정
    @OneToMany(mappedBy = "employee")
    private File profileImage; // 일단 바이너리 컨텐트라는 이름으로 두었음...

    Employee(String name, String email, Department department, String position, Instant hiredDate,
        File profileImage) {
        this.name = name;
        this.email = email;
        this.employeeNumber =
            "EMP-" + LocalDateTime.ofInstant(hiredDate, ZoneId.systemDefault()).getYear()
                + Instant.now().toEpochMilli();
        this.department = department;
        this.position = position;
        this.hiredDate = hiredDate;
        this.status = EmployeeStatus.ACTIVE;
        this.profileImage = profileImage;


    }


}
