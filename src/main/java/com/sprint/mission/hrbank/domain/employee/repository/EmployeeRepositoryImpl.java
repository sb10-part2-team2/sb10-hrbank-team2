package com.sprint.mission.hrbank.domain.employee.repository;

import static com.sprint.mission.hrbank.domain.department.QDepartment.department;
import static com.sprint.mission.hrbank.domain.employee.QEmployee.employee;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.hrbank.domain.employee.Employee;
import com.sprint.mission.hrbank.domain.employee.EmployeeStatus;
import com.sprint.mission.hrbank.domain.employee.dto.CursorPageResponseEmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeCountRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDistributionDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeSearchRequest;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendDto;
import com.sprint.mission.hrbank.domain.employee.dto.EmployeeTrendInterval;
import com.sprint.mission.hrbank.domain.employee.mapper.EmployeeMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final EmployeeMapper employeeMapper;

  @Override
  public CursorPageResponseEmployeeDto search(EmployeeSearchRequest req) {
    int size = req.size() > 0 ? req.size() : 10; // 사이즈 기본 값 설정. 사이즈가 0(비어있다면)이면 10으로 초기화

    // QuerydslConfig에서 Bean으로 설정해두었던 JPAQueryFactory를 통해 쿼리 생성 및 조합.
    List<Employee> rows = queryFactory
        .selectFrom(employee) // employee 에서 가져옴.
        .leftJoin(employee.department, department) // employee의 department도 left join
        .fetchJoin()// N + 1 을 해결하기 위해 department도 fetch join
        .where(
            nameOrEmailContains(req.nameOrEmail()), // 이름과 이메일이 포함되면
            departmentNameEq(req.departmentName()), // 입력한 부서 이름이 같다면
            positionEq(req.position()), // 입력한 직합이 같다면
            employeeNumberEq(req.employeeNumber()), // 입력한 사원 번호가 같다면
            hireDateGoe(req.hireDateFrom()), // 입력한 날짜보다 입사 일자가 greater or equal 이면
            hireDateLoe(req.hireDateTo()), // 입력한 날짜보다 입사 일자가 less or equal 이면
            statusEq(req.status()), // 입력한 상태 (재직중, 퇴사, 휴직중)이 맞다면
            cursorCondition(req.cursor()) // 커서 페이지네이션을 위한 조건에 맞는다면
        )
        .orderBy(employee.hireDate.desc(),
            employee.id.desc()) // 기본적으로 입사일 desc으로 정렬, 같을 경우 사원 id desc 정렬
        .limit(size + 1L) // 페이지네이션을 위해 limit에 1을 여분으로 추가
        .fetch(); // fetch

    boolean hasNext = rows.size() > size; // 가져온 행들의 크기(개수)가 지정한 사이즈보다 크다? -> 다음 페이지가 존재하구나...
    List<Employee> page =
        hasNext ? rows.subList(0, size) : rows; // size 10만큼만 잘라서 페이지로 보여준다. 다음 페이지 없을 시 그대로

    // 다음 커서를 null로 일단 초기화
    String nextCursor = null;
    Long nextIdAfter = null;

    // 다음 페이지가 있으면서 현재 페이지가 비어있지 않다면
    if (hasNext && !page.isEmpty()) {
      Employee last = page.get(page.size() - 1);
      nextCursor = last.getHireDate() + "|" + last.getId();
      nextIdAfter = last.getId();
    }

    List<EmployeeDto> content = page.stream()
        .map(employeeMapper::entityToDto)
        .toList();

    Long total = queryFactory
        .select(employee.count())
        .from(employee)
        .join(employee.department, department)
        .where(nameOrEmailContains(req.nameOrEmail()),
            departmentNameEq(req.departmentName()),
            positionEq(req.position()),
            employeeNumberEq(req.employeeNumber()),
            hireDateGoe(req.hireDateFrom()),
            hireDateLoe(req.hireDateTo()),
            statusEq(req.status())
        )
        .fetchOne();

    return new CursorPageResponseEmployeeDto(
        content,
        nextCursor,
        nextIdAfter,
        size,
        total == null ? 0L : total,
        hasNext
    );
  }

  @Override
  public long countEmployees(EmployeeCountRequest req) {
    BooleanExpression statusCondition;

    if (req.status() != null) {
      // 특정 상태가 요청된 경우 해당 상태로 필터링
      statusCondition = employee.status.eq(req.status());
    } else {
      // 상태 파라미터가 없는 경우, 명시적으로 재직(ACTIVE) 및 휴직(ON_LEAVE) 직원만 포함 (퇴사자 제외)
      statusCondition = employee.status.in(EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE);
    }

    Long count = queryFactory
        .select(employee.count())
        .from(employee)
        .where(
            statusCondition,
            hireDateGoe(req.fromDate()),
            hireDateLoe(req.toDate())
        )
        .fetchOne();

    return count == null ? 0L : count;
  }

  @Override
  public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
      EmployeeStatus status) {
    // 1. 전체 직원 수 조회 (비율 계산용)
    Long total = queryFactory
        .select(employee.count())
        .from(employee)
        .where(statusEq(status))
        .fetchOne();

    long totalCount = (total != null) ? total : 0L;

    // 2. 그룹별 직원 수 조회
    String normalizedGroupBy = (groupBy == null) ? "department" : groupBy.trim().toLowerCase();
    var path = switch (normalizedGroupBy) {
      case "department" -> department.name;
      case "position" -> employee.position;
      default -> throw new IllegalArgumentException("groupBy must be 'department' or 'position'");
    };

    List<com.querydsl.core.Tuple> results = queryFactory
        .select(path, employee.count())
        .from(employee)
        .leftJoin(employee.department, department)
        .where(statusEq(status))
        .groupBy(path)
        .fetch();

    return results.stream()
        .map(tuple -> {
          String key = tuple.get(path);
          Long countWrapper = tuple.get(employee.count());
          long count = (countWrapper != null) ? countWrapper : 0L;

          double percentage = (totalCount > 0) ? (double) count / totalCount * 100 : 0.0;
          // 소수점 첫째 자리까지 반올림
          percentage = Math.round(percentage * 10.0) / 10.0;
          return new EmployeeDistributionDto(key, count, percentage);
        })
        .toList();
  }

  @Override
  public List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to,
      EmployeeTrendInterval interval) {
    List<EmployeeTrendDto> result = new ArrayList<>();
    LocalDate current = from;
    long previousCount = -1;

    // from부터 to까지 interval 단위로 시계열 루프 실행 (빈 구간도 포함)
    while (!current.isAfter(to)) {
      final LocalDate snapshotDate = getSnapshotDate(current, interval);

      // 해당 시점의 총 직원 수 집계 (그 날짜 기준 입사자 중 퇴사자 제외)
      Long count = queryFactory
          .select(employee.count())
          .from(employee)
          .where(
              employee.hireDate.loe(snapshotDate),
              statusNotResigned()
          )
          .fetchOne();

      long currentCount = count != null ? count : 0L;
      long change = 0;
      double changeRate = 0.0;

      // 이전 시점과 비교하여 증감 및 증감률 계산
      if (previousCount != -1) {
        change = currentCount - previousCount;
        if (previousCount > 0) {
          changeRate = (double) change / previousCount * 100;
          changeRate = Math.round(changeRate * 10.0) / 10.0;
        } else if (change > 0) {
          changeRate = 100.0;
        }
      }

      result.add(new EmployeeTrendDto(snapshotDate, currentCount, change, changeRate));

      previousCount = currentCount;
      current = getNextDate(current, interval);
    }

    return result;
  }


  private LocalDate getSnapshotDate(LocalDate date, EmployeeTrendInterval interval) {
    return switch (interval) {
      case DAILY -> date;
      case WEEKLY -> date.with(java.time.DayOfWeek.SUNDAY);
      case MONTHLY -> date.withDayOfMonth(date.lengthOfMonth());
      case QUARTERLY -> {
        int month = date.getMonthValue();
        int lastMonthOfQuarter = ((month - 1) / 3 + 1) * 3;
        yield date.withMonth(lastMonthOfQuarter)
            .withDayOfMonth(date.withMonth(lastMonthOfQuarter).lengthOfMonth());
      }
      case YEARLY -> date.withDayOfYear(date.lengthOfYear());
    };
  }

  private LocalDate getNextDate(LocalDate date, EmployeeTrendInterval interval) {
    return switch (interval) {
      case DAILY -> date.plusDays(1);
      case WEEKLY -> date.plusWeeks(1);
      case MONTHLY -> date.plusMonths(1);
      case QUARTERLY -> date.plusMonths(3);
      case YEARLY -> date.plusYears(1);
    };
  }

  // 이름or이메일 필드가 포함되었는지 확인하고 없으면 null 리턴
  private BooleanExpression nameOrEmailContains(String keyword) {
    if (!StringUtils.hasText(keyword)) {
      return null;
    }
    return employee.name.containsIgnoreCase(keyword)
        .or(employee.email.containsIgnoreCase(keyword));
  }

  private BooleanExpression departmentNameEq(String departmentName) {
    return StringUtils.hasText(departmentName) ? department.name.eq(departmentName) : null;
  }

  private BooleanExpression positionEq(String position) {
    return StringUtils.hasText(position) ? employee.position.eq(position) : null;
  }

  private BooleanExpression employeeNumberEq(String employeeNumber) {
    return StringUtils.hasText(employeeNumber) ? employee.employeeNumber.eq(employeeNumber) : null;
  }

  private BooleanExpression hireDateGoe(LocalDate from) {
    if (from == null) {
      return null;
    }
    return employee.hireDate.goe(from);
  }

  private BooleanExpression hireDateLoe(LocalDate to) {
    if (to == null) {
      return null;
    }
    return employee.hireDate.loe(to);
  }

  private BooleanExpression statusEq(EmployeeStatus status) {
    return status != null ? employee.status.eq(status) : null;
  }

  private BooleanExpression statusNotResigned() {
    return employee.status.ne(EmployeeStatus.RESIGNED);
  }

  private BooleanExpression cursorCondition(String cursor) {
    if (!StringUtils.hasText(cursor)) {
      return null;
    }

    // cursor 형식: "2023-01-01|20" (hireDate|id)
    String[] parts = cursor.split("\\|");
    if (parts.length != 2) {
      return null;
    }

    LocalDate lastHireDate = LocalDate.parse(parts[0]);
    Long lastId = Long.parseLong(parts[1]);

    return employee.hireDate.lt(lastHireDate)
        .or(employee.hireDate.eq(lastHireDate).and(employee.id.lt(lastId)));
  }


}


