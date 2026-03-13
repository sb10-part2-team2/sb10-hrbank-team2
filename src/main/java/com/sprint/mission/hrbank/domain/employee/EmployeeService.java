package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import java.time.Instant;
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

    }

    public EmployeeDto create(
        EmployeeCreateRequest req,
        MultipartFile profile
    ){
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

        Employee employee = new Employee(
            req.name(),
            req.email(),


        )


        return new EmployeeDto(
            req.name(),
            req.email(),

        )
    }
}
