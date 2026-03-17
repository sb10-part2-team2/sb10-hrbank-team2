package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.department.Department;
import com.sprint.mission.hrbank.domain.employee.Employee;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import com.sprint.mission.hrbank.domain.file.service.FileService;
import jakarta.persistence.EntityManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/*
 * [실제 백업 파일 생성 담당 서비스]
 * STEP.3 요구사항 반영:
 * - 전체 직원 정보를 CSV 파일로 생성
 * - OOM 방지를 위해 전체 직원을 한 번에 조회하지 않고 id > lastId 기반 배치 조회
 * - CSV는 BufferedWriter로 디스크에 바로 기록
 */

@Service
@RequiredArgsConstructor
public class BackupCsvWriter {

  private static final DateTimeFormatter FILE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneOffset.UTC);

  private final EntityManager entityManager;
  private final FileService fileService;

  @Value("${hrbank.storage.local.root-path}")
  private Path storageRootPath;

  @Value("${hrbank.backup.batch-size:500}")
  private int batchSize;

  // 전체 직원을 CSV로 저장
  @Transactional
  public StoredFile writeEmployeeBackupCsv(Long backupId, Instant startedAt) throws IOException {
    // 디렉토리 생성 (존재하지 않을 경우)
    Path filesDir = storageRootPath.resolve("files");
    Files.createDirectories(filesDir);

    // 저장용 파일명 생성
    String timestamp = FILE_TIME_FORMATTER.format(startedAt);
    String originalFilename = String.format("employee_backup_%d_%s.csv", backupId, timestamp);

    // 실제 디스크에 저장될 물리적 파일명 생성 (파일 중복 충돌 방지)
    String storedName = UUID.randomUUID() + "-" + originalFilename;
    Path csvPath = filesDir.resolve(storedName);

    // BufferedWriter를 사용하여 데이터를 메모리에 쌓지 않고 디스크로 직접 스트리밍
    try (BufferedWriter writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
      // 1. CSV 헤더 작성
      writer.write(
          "id,name,email,employeeNumber,departmentId,departmentName,position,hireDate,status");
      writer.newLine();

      Long lastId = 0L;
      while (true) {
        // 2. id > lastId 기반의 배치 조회 (OOM 방지)
        List<Employee> employees = entityManager.createQuery(
                "SELECT e FROM Employee e WHERE e.id > :lastId ORDER BY e.id ASC", Employee.class)
            .setParameter("lastId", lastId)
            .setMaxResults(batchSize)
            .getResultList();

        if (employees.isEmpty()) {
          break;
        }

        // 3. 데이터를 한 줄씩 CSV로 변환하여 기록
        for (Employee employee : employees) {
          writer.write(toCsvLine(employee));
          writer.newLine();
        }

        // 버퍼의 내용을 물리 디스크에 기록
        writer.flush();

        // 현재 배치에서 사용한 엔티티들을 영속성 컨텍스트에서 통째로 제거
        entityManager.clear();

        // 다음 배치를 위해 마지막으로 처리된 ID 갱신
        lastId = employees.get(employees.size() - 1).getId();
      }

      // 4. 생성된 파일을 FileService를 통해 DB 메타데이터로 등록
      return fileService.registerExistingFile(originalFilename, "text/csv", csvPath);

    } catch (Exception e) {
      // 파일 생성 중 실패 시 잔여 파일 삭제
      Files.deleteIfExists(csvPath);
      throw e;
    }
  }

  // 에러 발생 시 .log 파일 생성
  @Transactional
  public StoredFile writeErrorLog(Long backupId, String workerIp, Instant startedAt,
      Exception exception)
      throws IOException {
    // 디렉토리 생성 (존재하지 않을 경우)
    Path filesDir = storageRootPath.resolve("files");
    Files.createDirectories(filesDir);

    // 저장용 파일명 생성
    String timestamp = FILE_TIME_FORMATTER.format(startedAt);
    String originalFilename = String.format("error_log_%d_%s.log", backupId, timestamp);

    // 실제 디스크에 저장될 물리적 파일명 생성 (파일 중복 충돌 방지)
    Path logPath = filesDir.resolve(UUID.randomUUID() + "-" + originalFilename);

    try {
      // 스택 트레이스를 문자열로 추출
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      String content = String.format(
          "Backup ID: %d\nWorker: %s\nStartedAt: %s\nError: %s\n\nStackTrace:\n%s",
          backupId, workerIp, startedAt, exception.getMessage(), sw.toString());

      // 파일 생성
      Files.writeString(logPath, content, StandardCharsets.UTF_8);

      // 생성된 파일을 FileService를 통해 DB 메타데이터로 등록
      return fileService.registerExistingFile(originalFilename, "text/plain", logPath);

    } catch (Exception e) {
      // 파일 생성 중 실패 시 잔여 파일 삭제
      Files.deleteIfExists(logPath);
      throw e;
    }
  }

  // ----- 헬퍼 메서드 -----
  // 직원 엔티티를 CSV 데이터 행으로 변환
  private String toCsvLine(Employee employee) {
    Department dept = employee.getDepartment();
    return String.join(",",
        String.valueOf(employee.getId()),
        escapeCsv(employee.getName()),
        escapeCsv(employee.getEmail()),
        escapeCsv(employee.getEmployeeNumber()),
        dept != null ? String.valueOf(dept.getId()) : "",
        escapeCsv(dept != null ? dept.getName() : ""),
        escapeCsv(employee.getPosition()),
        employee.getHireDate() != null ? employee.getHireDate().toString() : "",
        employee.getStatus() != null ? employee.getStatus().name() : ""
    );
  }

  // CSV 특수문자(쉼표, 따옴표, 줄바꿈) 이스케이프 처리
  private String escapeCsv(String value) {
    if (!StringUtils.hasText(value)) {
      return "";
    }
    // 큰따옴표가 포함된 경우 두 개("")로 치환
    String escaped = value.replace("\"", "\"\"");
    // 쉼표나 줄바꿈이 포함된 경우 값 전체를 큰따옴표로 감쌈
    if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")
        || escaped.contains("\r")) {
      return "\"" + escaped + "\"";
    }
    return escaped;
  }
}