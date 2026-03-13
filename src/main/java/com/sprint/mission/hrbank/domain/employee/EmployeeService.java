package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmployeeService {

    public CursorPageResponseEmployeeDto getEmployees(
        String nameOrEmail,
        String departmentName,
        String position,
        String employeeNumber,
        Instant hireDateFrom,
        Instant hireDateTo,
        EmployeeStatus status,
        String cursor,
        Long idAfter,
        int size,
        String sortField,
        String sortDirection
    ) {
        // TODO: 추후 구현 예정
    }

    public EmployeeDto create(EmployeeCreateRequest req, MultipartFile profile) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

        Department department = departmentRepository.findById(
            req.departmentId()); // 추후 Department 부분과 연계

        //File file = null;

        //TODO: 추후 FILE 부분 완성이 되면 구현 예정입니다.
//        if (profile != null) {
//            file = new File();
//        }

        Employee employee = new Employee(
            req.name(),
            req.email(),
            department,
            req.position(),
            req.hireDate(),
            null // 일단 null을 넣엇습니다.
            //file
        );

        Employee saved = employeeRepository.save(employee); // 레포지토리 인터페이스를 통해 영속화

        return new EmployeeDto(
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getEmployeeNumber(),
            saved.getDepartment().getId(),
            saved.getDepartment().getName(),
            saved.getPosition(),
            saved.getHiredDate(),
            saved.getStatus(),
            saved.getProfileImage()
        );
    }
}
