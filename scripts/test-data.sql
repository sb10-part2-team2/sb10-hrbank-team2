-- HR Bank test dataset (PostgreSQL)
-- Usage:
--   psql "$DB_URL" -U "$DB_USERNAME" -f scripts/test-data.sql

BEGIN;

-- Clean up in FK-safe order
TRUNCATE TABLE change_log_diffs RESTART IDENTITY CASCADE;
TRUNCATE TABLE change_logs RESTART IDENTITY CASCADE;
TRUNCATE TABLE backups RESTART IDENTITY CASCADE;
TRUNCATE TABLE employees RESTART IDENTITY CASCADE;
TRUNCATE TABLE department RESTART IDENTITY CASCADE;
TRUNCATE TABLE files RESTART IDENTITY CASCADE;

-- Files (for profile images / backup files)
INSERT INTO files (original_name, stored_name, content_type, size, storage_path, created_at)
VALUES
  ('kim-yuna.png', 'f_001_kim_yuna.png', 'image/png', 182044, '.hrbank/storage/f_001_kim_yuna.png', NOW()),
  ('park-minsu.png', 'f_002_park_minsu.png', 'image/png', 204876, '.hrbank/storage/f_002_park_minsu.png', NOW()),
  ('backup-2026-03-18.csv', 'backup_20260318_120000.csv', 'text/csv', 90812, '.hrbank/storage/backup_20260318_120000.csv', NOW());

-- Departments
INSERT INTO department (name, description, established_date, created_at, updated_at)
VALUES
  ('인사팀', '채용, 평가, 보상/복리후생 담당', DATE '2018-01-15', NOW(), NOW()),
  ('개발팀', '백엔드/프론트엔드/인프라 개발', DATE '2019-04-01', NOW(), NOW()),
  ('재무팀', '회계, 세무, 자금 운용 담당', DATE '2017-07-10', NOW(), NOW()),
  ('운영팀', '서비스 운영 및 내부 프로세스 관리', DATE '2020-03-02', NOW(), NOW());

-- Employees
-- NOTE: employee.status is ORDINAL (no @Enumerated on entity)
-- ACTIVE=0, ON_LEAVE=1, RESIGNED=2
INSERT INTO employees (
  name, email, employee_number, department_id, position, hire_date, status, files_id, created_at, updated_at
)
VALUES
  ('김유나', 'yuna.kim@hrbank.local', 'EMP-202303010001', 1, 'HR Manager', DATE '2023-03-01', 0, 1, NOW(), NOW()),
  ('박민수', 'minsu.park@hrbank.local', 'EMP-202404150002', 2, 'Backend Engineer', DATE '2024-04-15', 0, 2, NOW(), NOW()),
  ('이서진', 'seojin.lee@hrbank.local', 'EMP-202205200003', 2, 'Frontend Engineer', DATE '2022-05-20', 1, NULL, NOW(), NOW()),
  ('최지훈', 'jihoon.choi@hrbank.local', 'EMP-202101110004', 3, 'Accountant', DATE '2021-01-11', 0, NULL, NOW(), NOW()),
  ('정하늘', 'haneul.jeong@hrbank.local', 'EMP-202006220005', 4, 'Operations Lead', DATE '2020-06-22', 2, NULL, NOW(), NOW());

-- Change logs
INSERT INTO change_logs (
  employee_id, type, employee_number_snapshot, memo, ip_address, employee_name_snapshot, profile_image_id_snapshot, created_at
)
VALUES
  (2, 'UPDATED', 'EMP-202404150002', '직함 변경', '10.0.0.21', '박민수', 2, NOW() - INTERVAL '5 days'),
  (3, 'UPDATED', 'EMP-202205200003', '휴직 처리', '10.0.0.34', '이서진', NULL, NOW() - INTERVAL '2 days'),
  (5, 'DELETED', 'EMP-202006220005', '퇴사 처리', '10.0.0.55', '정하늘', NULL, NOW() - INTERVAL '1 day');

INSERT INTO change_log_diffs (change_log_id, property_name, before_value, after_value, created_at)
VALUES
  (1, 'position', 'Software Engineer', 'Backend Engineer', NOW() - INTERVAL '5 days'),
  (2, 'status', 'ACTIVE', 'ON_LEAVE', NOW() - INTERVAL '2 days'),
  (3, 'status', 'ACTIVE', 'RESIGNED', NOW() - INTERVAL '1 day');

-- Backups
INSERT INTO backups (
  worker, started_at, ended_at, status, backup_file_id, error_summary, in_progress_status, version, created_at
)
VALUES
  ('scheduler@node-1', NOW() - INTERVAL '8 hours', NOW() - INTERVAL '8 hours' + INTERVAL '12 seconds', 'COMPLETED', 3, NULL, NULL, 0, NOW() - INTERVAL '8 hours'),
  ('scheduler@node-1', NOW() - INTERVAL '1 hours', NOW() - INTERVAL '1 hours' + INTERVAL '9 seconds', 'FAILED', NULL, 'CSV write timeout', NULL, 0, NOW() - INTERVAL '1 hours');

COMMIT;

