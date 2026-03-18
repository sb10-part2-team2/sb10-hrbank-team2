package com.sprint.mission.hrbank.domain.backup;

import com.sprint.mission.hrbank.domain.backup.dto.BackupDto;
import com.sprint.mission.hrbank.domain.backup.dto.BackupSearchRequest;
import com.sprint.mission.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import com.sprint.mission.hrbank.domain.changelog.repository.ChangeLogRepository;
import com.sprint.mission.hrbank.domain.file.entity.StoredFile;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BackupService {

  private final BackupRepository backupRepository;
  private final ChangeLogRepository changeLogRepository;
  private final BackupCommandService backupCommandService;
  private final BackupCsvWriter backupCsvWriter;
  private final BackupMapper backupMapper;

  public CursorPageResponseBackupDto getBackups(BackupSearchRequest request) {
    // startedAt* 범위 조건이 올바른지 검증
    validateStartedAtRange(request.startedAtFrom(), request.startedAtTo());

    // 요청 파라미터 정규화
    int size = normalizeSize(request.size());
    String sortField = normalizeSortField(request.sortField());
    Direction sortDirection = normalizeSortDirection(request.sortDirection());
    String worker = normalizeWorker(request.worker());

    // 정렬 조건 생성
    // 1차 정렬 : 사용자가 선택한 필드 (정렬방향, 정렬필드)
    // 2차 정렬 : id
    Sort sort = Sort.by(sortDirection, sortField) // 1차 정렬 (sortField를 sortDirection 방향으로 정렬)
        .and(Sort.by(sortDirection, "id")); // 같은 값일 경우 id 기준 정렬
    // id를 sortDirection 방향으로 정렬

    // Pageable 생성
    Pageable pageable = PageRequest.of(0, size, sort); // 페이지 번호, 페이지 크기, 정렬 규칙 저장

    // JPQL 조회 - 실제 목록 데이터를 페이지 단위로 조회
    Slice<Backup> backupSlices;
    if (sortDirection == Direction.ASC) {
      backupSlices = backupRepository.searchBackupsAsc(
          worker,
          request.status(),
          request.startedAtFrom(),
          request.startedAtTo(),
          request.idAfter(),
          pageable
      );
    } else {
      backupSlices = backupRepository.searchBackupsDesc(
          worker,
          request.status(),
          request.startedAtFrom(),
          request.startedAtTo(),
          request.idAfter(),
          pageable
      );
    }

    // DTO 변환
    List<BackupDto> content = backupSlices.getContent().stream()
        .map(backupMapper::toDto)
        .toList();

    // totalElements 계산
    long totalElements = backupRepository.countByConditions(
        worker,
        request.status(),
        request.startedAtFrom(),
        request.startedAtTo()
    );

    // nextIdAfter 계산 -> 현재 페이지 마지막 행의 id
    Long nextIdAfter = (backupSlices.hasNext())
        ? content.get(content.size() - 1).id()
        : null; // 다음 페이지 없는 경우 -> null

    // nextCursor 계산
    String nextCursor;
    if (nextIdAfter != null) { // 다음 페이지가 있으면
      Backup last = backupSlices.getContent().get(backupSlices.getNumberOfElements() - 1);
      // last -> 다음 페이지의 기준점이 되는 마지막 행
      nextCursor = buildNextCursor(sortField, last, nextIdAfter);
    } else {
      nextCursor = null;
    }

    return new CursorPageResponseBackupDto(
        content,
        nextCursor,
        nextIdAfter,
        pageable.getPageSize(),
        totalElements,
        backupSlices.hasNext()
    );
  }

  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public Backup createBackup(String workerIp) {
    // 1. 백업 필요 여부 판단
    Optional<Backup> last = backupRepository.findFirstByStatusOrderByEndedAtDesc(
        BackupStatus.COMPLETED);
    if (!checkDataChangedSince(last)) {
      return backupCommandService.createSkipped(workerIp);
    }

    // 2. IN_PROCESS 상태를 가진 백업 이력 생성 (트랜잭션 분리)
    Backup inProgress = backupCommandService.createInProgress(workerIp);

    try {
      // 3. csv 파일 생성
      StoredFile file = backupCsvWriter.writeEmployeeBackupCsv(
          inProgress.getId(),
          inProgress.getStartedAt()
      );

      // 4. [요구사항 Step 4-1] 성공 처리 (트랜잭션 분리)
      backupCommandService.markCompleted(inProgress.getId(), file);

    } catch (Exception e) {
      // 5. [요구사항 Step 4-2] 실패 처리
      StoredFile logFile = null;
      try {
        // 에러 로그 파일 생성
        logFile = backupCsvWriter.writeErrorLog(inProgress.getId(), workerIp,
            inProgress.getStartedAt(), e);
      } catch (Exception ignored) {
        // 에러 로그 파일 생성도 실패할 경우, 예외를 무시하고 백업 실패 처리 진행
      }

      // 에러 메시지 요약
      String errorSummary = summarizeException(e);
      // 최종 실패 상태 기록 (트랜잭션 분리)
      backupCommandService.markFailed(inProgress.getId(), logFile, errorSummary);
    }

    // 최종 상태 반영된 결과 조회해서 반환
    return backupRepository.findById(inProgress.getId())
        .orElseThrow(
            () -> new IllegalStateException("존재하지 않는 백업 이력입니다. ID: " + inProgress.getId()));
  }

  // 대시보드 마지막 백업 정보 조회용
  public Optional<BackupDto> getLatestBackup(BackupStatus status) {
    BackupStatus searchStatus = (status == null) ? BackupStatus.COMPLETED : status;
    return backupRepository.findFirstByStatusOrderByEndedAtDesc(searchStatus)
        .map(backupMapper::toDto);
  }

  // ----- 헬퍼 메서드 -----
  // 백업 시점 이후에 데이터 변경이 있었는지 판단
  private boolean checkDataChangedSince(Optional<Backup> lastBackup) {
    if (lastBackup.isEmpty() || lastBackup.get().getEndedAt() == null) {
      return true;
    }

    Instant lastBackupTime = lastBackup.get().getEndedAt();
    Instant now = Instant.now();

    // countChangeLogs를 활용하여 변경 이력 존재 여부 확인
    long changedCount = changeLogRepository.countChangeLogs(lastBackupTime, now);
    return changedCount > 0;
  }

  // 예외 메시지가 너무 길 경우 DB 저장 시 에러가 날 수 있어 안전하게 요약
  private String summarizeException(Exception e) {
    String message = e.getMessage();
    if (message == null) {
      return "Unknown Error occurred during backup.";
    }
    // 보통 DB의 String/Varchar 컬럼 크기인 255자를 기준으로 안전하게 200자 내외로 자름
    return message.length() > 200 ? message.substring(0, 200) + "..." : message;
  }

  // size 정규화
  private int normalizeSize(Integer size) {
    if (size == null) {
      return 10; // 기본값 10
    }
    if (size < 1) {
      throw new IllegalArgumentException("size는 1 이상이어야 합니다");
    }
    return size;
  }

  // sortField 정규화
  private String normalizeSortField(String sortField) {
    if (!StringUtils.hasText(sortField)) {
      return "startedAt"; // 기본값 'startedAt'
    }
    if (!"startedAt".equals(sortField)
        && !"endedAt".equals(sortField)
        && !"status".equals(sortField)
    ) {
      throw new IllegalArgumentException("sortField는 startedAt, endedAt, status만 허용됩니다");
    }
    return sortField;
  }

  // normalizeSortDirection 정규화
  private Sort.Direction normalizeSortDirection(String sortDirection) {
    if (!StringUtils.hasText(sortDirection)) {
      return Sort.Direction.DESC; // 기본값 desc
    }
    if ("asc".equalsIgnoreCase(sortDirection)) {
      return Sort.Direction.ASC;
    }
    if ("desc".equalsIgnoreCase(sortDirection)) {
      return Sort.Direction.DESC;
    }
    throw new IllegalArgumentException("sortDirection은 ASC 또는 DESC만 허용됩니다");
  }

  // worker 정규화
  private String normalizeWorker(String worker) {
    return StringUtils.hasText(worker) ? worker : null;
  }

  // startedAt* 범위 조건이 올바른지 검증
  private void validateStartedAtRange(Instant startedAtFrom, Instant startedAtTo) {
    if (startedAtFrom != null && startedAtTo != null && startedAtFrom.isAfter(startedAtTo)) {
      throw new IllegalArgumentException("startedAtFrom은 startedAtTo보다 늦을 수 없습니다.");
    }
  }

  // nextCursor 생성
  // fallBackId -> endedAt이 null일 때 대체 커서 값으로 쓸 id
  private String buildNextCursor(String sortField, Backup backup, Long fallbackId) {
    switch (sortField) {
      case "endedAt": // endedAt 기준 정렬
        if (backup.getEndedAt() != null) {
          return backup.getEndedAt().toString();
        } else { // endedAt 값 없을 경우
          return String.valueOf(fallbackId); // 대체 커서 값 사용
        }
      case "status": // status 기준 정렬
        return backup.getStatus().name();
      default: // startedAt 기준 정렬
        return backup.getStartedAt().toString(); // startedAt은 null 값 안 들어옴
    }
  }
}
