package com.sprint.mission.hrbank.domain.employee;

import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCreateRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import java.awt.Cursor;
import java.time.Instant;
import lombok.Builder.Default;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping()
    public ResponseEntity<CursorPageResponseEmployeeDto> getEmployees(
        @RequestParam String nameOrEmail, // 직원 이름 또는 이메일
        @RequestParam String employeeNumber, // 사원 번호
        @RequestParam String departmentName, // 부서 이름
        @RequestParam String position, // 직함
        @RequestParam Instant hireDateFrom, // 입사일 시작
        @RequestParam Instant hireDateTo, // 입사일 종료
        @RequestParam EmployeeStatus status, // 상태 (재직중, 휴직중, 퇴사)
        @RequestParam Long idAfter, // 이전 페이지 마지막 요소 ID
        @RequestParam String cursor, // 커서 (다음 페이지 시작점)
        @RequestParam(defaultValue = "10") int size, // 페이지 크기 (기본값: 10)
        @RequestParam(defaultValue = "name") String sortField, // 정렬 필드 (기본값: name)
        @RequestParam(defaultValue = "asf") String sortDirection // 정렬 방향 (기본값: 오름차)
    ) {
        CursorPageResponseEmployeeDto response = employeeService.getEmployees(
            nameOrEmail,
            departmentName,
            position,
            employeeNumber,
            hireDateFrom,
            hireDateTo,
            status,
            cursor,
            idAfter,
            size,
            sortField,
            sortDirection
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(
        @RequestBody EmployeeCreateRequest req,
        @RequestPart MultipartFile profile) {
        return employeeService.create(req, profile); // 추후 구현 예정
    }


}
