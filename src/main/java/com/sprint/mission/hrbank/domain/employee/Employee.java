package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.baseentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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

    @ManyToOne
    @JoinColumn(name = "files_id")
    private BinaryContent profileImage;

    @PrePersist
    void createEmployeeNumber() {
        Instant now = Instant.now();
        int year = LocalDateTime.ofInstant(hiredDate, ZoneId.systemDefault()).getYear();
        long timestamp = now.toEpochMilli();

        this.employeeNumber = "EMP-" + year + "-" + timestamp;
    }

}