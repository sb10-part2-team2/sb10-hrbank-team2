-- 1. 부서 데이터
INSERT INTO department (name, description, established_date, created_at, updated_at)
VALUES ('백엔드 개발팀', '서버 및 DB 설계를 담당하는 부서입니다.', '2023-01-01', TIMESTAMP '2023-01-01 00:00:00',
        TIMESTAMP '2023-01-01 00:00:00'),
       ('프론트엔드 개발팀', '사용자 인터페이스 및 웹 앱 개발을 담당합니다.', '2023-01-01', TIMESTAMP '2023-01-01 00:00:00',
        TIMESTAMP '2023-01-01 00:00:00'),
       ('인프라 운영팀', '클라우드 환경 및 서버 보안을 관리합니다.', '2023-06-01', TIMESTAMP '2023-06-01 00:00:00',
        TIMESTAMP '2023-06-01 00:00:00'),
       ('UI/UX 디자인팀', '서비스 디자인 및 사용자 경험 설계를 담당합니다.', '2023-01-01', TIMESTAMP '2023-01-01 00:00:00',
        TIMESTAMP '2023-01-01 00:00:00'),
       ('경영지원팀', '인사, 총무 및 조직 문화를 관리합니다.', '2023-01-01', TIMESTAMP '2023-01-01 00:00:00',
        TIMESTAMP '2023-01-01 00:00:00');

-- 2. 파일 데이터
INSERT INTO files (id, original_name, stored_name, storage_path, content_type, size, created_at)
VALUES (1, 'profile_default.jpg', 'stored_profile_1.jpg', '.hrbank/storage/stored_profile_1.jpg',
        'image/jpeg', 15000, CURRENT_TIMESTAMP),
       (2, 'backup_full_20260318.csv', 'stored_backup_1.csv', '.hrbank/storage/stored_backup_1.csv',
        'text/csv', 45000, CURRENT_TIMESTAMP);

-- 3. 직원 데이터 (평범한 이름 및 추이 분석용 날짜 분산)
-- 2024년
INSERT INTO employees (name, email, employee_number, department_id, position, hire_date, status,
                       created_at, updated_at)
VALUES ('김철수', 'chulsoo.kim@gmail.com', 'EMP-20240101-001', 1, '부장', '2024-01-01', 'ACTIVE',
        TIMESTAMP '2024-01-01 09:00:00', TIMESTAMP '2024-01-01 09:00:00'),
       ('이영희', 'younghee.lee@gmail.com', 'EMP-20240315-001', 2, '과장', '2024-03-15', 'ACTIVE',
        TIMESTAMP '2024-03-15 09:00:00', TIMESTAMP '2024-03-15 09:00:00'),
       ('박민준', 'minjun.park@gmail.com', 'EMP-20240710-001', 1, '대리', '2024-07-10', 'ACTIVE',
        TIMESTAMP '2024-07-10 09:00:00', TIMESTAMP '2024-07-10 09:00:00'),
       ('최지우', 'jiwoo.choi@gmail.com', 'EMP-20241120-001', 4, '대리', '2024-11-20', 'RESIGNED',
        TIMESTAMP '2024-11-20 09:00:00', TIMESTAMP '2025-05-20 18:00:00');

-- 2025년
INSERT INTO employees (name, email, employee_number, department_id, position, hire_date, status,
                       created_at, updated_at)
VALUES ('정다은', 'daeun.jung@gmail.com', 'EMP-20250110-001', 5, '사원', '2025-01-10', 'ACTIVE',
        TIMESTAMP '2025-01-10 09:00:00', TIMESTAMP '2025-01-10 09:00:00'),
       ('강건우', 'geonwoo.kang@gmail.com', 'EMP-20250405-001', 3, '대리', '2025-04-05', 'ACTIVE',
        TIMESTAMP '2025-04-05 09:00:00', TIMESTAMP '2025-04-05 09:00:00'),
       ('윤서연', 'seoyeon.yoon@gmail.com', 'EMP-20250615-001', 2, '사원', '2025-06-15', 'ACTIVE',
        TIMESTAMP '2025-06-15 09:00:00', TIMESTAMP '2025-06-15 09:00:00'),
       ('임재현', 'jaehyun.lim@gmail.com', 'EMP-20250901-001', 1, '사원', '2025-09-01', 'ON_LEAVE',
        TIMESTAMP '2025-09-01 09:00:00', TIMESTAMP '2026-01-01 09:00:00'),
       ('한승우', 'seungwoo.han@gmail.com', 'EMP-20251110-001', 4, '사원', '2025-11-10', 'ACTIVE',
        TIMESTAMP '2025-11-10 09:00:00', TIMESTAMP '2025-11-10 09:00:00');

-- 2026년
INSERT INTO employees (name, email, employee_number, department_id, position, hire_date, status,
                       created_at, updated_at)
VALUES ('조현우', 'hyunwoo.cho@gmail.com', 'EMP-20260105-001', 1, '신입', '2026-01-05', 'ACTIVE',
        TIMESTAMP '2026-01-05 09:00:00', TIMESTAMP '2026-01-05 09:00:00'),
       ('배수지', 'suzy.bae@gmail.com', 'EMP-20260210-001', 2, '신입', '2026-02-10', 'ACTIVE',
        TIMESTAMP '2026-02-10 09:00:00', TIMESTAMP '2026-02-10 09:00:00'),
       ('남주혁', 'joohyuk.nam@gmail.com', 'EMP-20260301-001', 3, '신입', '2026-03-01', 'ACTIVE',
        TIMESTAMP '2026-03-01 09:00:00', TIMESTAMP '2026-03-01 09:00:00');

-- 4. 수정 이력 (ChangeLog) - 직원 관련 전체 시나리오
-- (1) 신규 추가 (CREATED)
INSERT INTO change_logs (id, employee_id, type, employee_number_snapshot, employee_name_snapshot,
                         memo, ip_address, created_at)
VALUES (1, 1, 'CREATED', 'EMP-20240101-001', '김철수', '신규 입사자 등록', '127.0.0.1',
        TIMESTAMP '2024-01-01 09:00:00'),
       (2, 2, 'CREATED', 'EMP-20240315-001', '이영희', '신규 입사자 등록', '127.0.0.1',
        TIMESTAMP '2024-03-15 09:00:00');

-- (2) 정보 수정 (UPDATED) - 부서 이동
INSERT INTO change_logs (id, employee_id, type, employee_number_snapshot, employee_name_snapshot,
                         memo, ip_address, created_at)
VALUES (10, 3, 'UPDATED', 'EMP-20240710-001', '박민준', '부서 변경: 경영지원팀 -> 백엔드 개발팀', '192.168.1.5',
        TIMESTAMP '2025-02-01 10:30:00');
INSERT INTO change_log_diffs (change_log_id, property_name, before_value, after_value)
VALUES (10, 'department', '경영지원팀', '백엔드 개발팀');

-- (3) 정보 수정 (UPDATED) - 상태 변경
INSERT INTO change_logs (id, employee_id, type, employee_number_snapshot, employee_name_snapshot,
                         memo, ip_address, created_at)
VALUES (11, 8, 'UPDATED', 'EMP-20250901-001', '임재현', '개인 사정으로 인한 휴직 처리', '127.0.0.1',
        TIMESTAMP '2026-01-01 09:00:00');
INSERT INTO change_log_diffs (change_log_id, property_name, before_value, after_value)
VALUES (11, 'status', 'ACTIVE', 'ON_LEAVE');

-- (4) 정보 수정 (UPDATED) - 직함 승진
INSERT INTO change_logs (id, employee_id, type, employee_number_snapshot, employee_name_snapshot,
                         memo, ip_address, created_at)
VALUES (12, 2, 'UPDATED', 'EMP-20240315-001', '이영희', '정기 승진 발령', '192.168.1.10',
        TIMESTAMP '2026-03-01 09:00:00');
INSERT INTO change_log_diffs (change_log_id, property_name, before_value, after_value)
VALUES (12, 'position', '과장', '차장');

-- (5) 직원 삭제 (DELETED)
INSERT INTO change_logs (id, employee_id, type, employee_number_snapshot, employee_name_snapshot,
                         memo, ip_address, created_at)
VALUES (20, 999, 'DELETED', 'EMP-20230505-099', '삭제된직원', '부적절한 정보로 인한 삭제', '127.0.0.1',
        TIMESTAMP '2026-03-10 15:00:00');

-- 5. 백업 이력 (Backups)
INSERT INTO backups (id, worker, started_at, ended_at, status, backup_file_id, version, created_at)
VALUES (1, '127.0.0.1', TIMESTAMP '2026-01-01 03:00:00', TIMESTAMP '2026-01-01 03:05:00',
        'COMPLETED', 2, 0, TIMESTAMP '2026-01-01 03:00:00'),
       (2, '192.168.1.10', TIMESTAMP '2026-02-01 03:00:00', TIMESTAMP '2026-02-01 03:02:00',
        'COMPLETED', 2, 0, TIMESTAMP '2026-02-01 03:00:00'),
       (3, '127.0.0.1', TIMESTAMP '2026-03-18 12:00:00', TIMESTAMP '2026-03-18 12:01:00', 'FAILED',
        null, 0, TIMESTAMP '2026-03-18 12:00:00');

-- 6. PostgreSQL 시퀀스 초기화 (에러 방지용)
SELECT setval('department_id_seq', COALESCE((SELECT max(id) FROM department), 1), true);
SELECT setval('employees_id_seq', COALESCE((SELECT max(id) FROM employees), 1), true);
SELECT setval('files_id_seq', COALESCE((SELECT max(id) FROM files), 1), true);
SELECT setval('change_logs_id_seq', COALESCE((SELECT max(id) FROM change_logs), 1000), true);
SELECT setval('backups_id_seq', COALESCE((SELECT max(id) FROM backups), 1), true);
