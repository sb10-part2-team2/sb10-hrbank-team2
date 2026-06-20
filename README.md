# HR Bank

### [팀 노션 페이지 링크](https://www.notion.so/2981d245f2c083ecb1a381cdf3ec9e8b?source)

---

## 팀원 구성

|                                           프로필                                           |                    이름                    |               역할               | 담당 기능                                                                                                                                      |
|:---------------------------------------------------------------------------------------:|:----------------------------------------:|:------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------|
|  <img src="https://github.com/conradrado.png" width="100" style="border-radius:50%;">   | **[김성경](https://github.com/conradrado)** |   **직원 관리,<br>팀장, 시연 영상 제작**   | - 직원 등록 API<br>- 직원 수정 API<br>- 직원 삭제 API<br>- 직원 단건 조회 API<br>- 직원 목록 조회 API |
|    <img src="https://github.com/parkngg.png" width="100" style="border-radius:50%;">    |  **[박나경](https://github.com/parkngg)**   |   **데이터 백업 관리, <br>ERD 제작**    | - 백업 목록 조회 API<br>- 백업 수동 실행 API<br>- 최신 백업 조회 API<br>- 직원 데이터 CSV 파일 생성 및 저장 |
|    <img src="https://github.com/raonPsm.png" width="100" style="border-radius:50%;">    |  **[박승민](https://github.com/raonPsm)**   |  **파일 관리, <br>노션 정리, PPT 제작**  | - 파일 다운로드 API<br>- 프로필 이미지 및 백업 CSV 파일 저장<br>- 파일 메타데이터 분리 저장<br>- Spring Scheduler 기반 주기적 자동 백업 |
| <img src="https://github.com/JeongSooHyeon.png" width="100" style="border-radius:50%;"> | **[정수현](https://github.com/JeongSooHyeon)**  | **직원 정보 수정 이력 관리, <br>PPT 제작** | - 이력 목록 조회 API<br>- 이력 상세 조회 API<br>- 이력 건수 조회 API<br>- 직원 생성·수정·삭제 시 자동 이력 기록<br>- 변경 전후 필드 단위 diff 저장 |
|   <img src="https://github.com/Amperisk9.png" width="100" style="border-radius:50%;">   | **[조성진](https://github.com/Amperisk9)** | **부서 관리, 배포 세팅 <br>**  | - 부서 등록 API<br>- 부서 수정 API<br>- 부서 삭제 API<br>- 부서 단건 조회 API<br>- 부서 목록 조회 API<br>- 부서 이름 중복 검증 로직 구현 |
|     <img src="https://github.com/CHH01.png" width="100" style="border-radius:50%;">     | **[최현호](https://github.com/CHH01)** |    **대시보드 관리, <br>ERD 제작**     | - 직원 수 조회 API<br>- 직원 분포 통계 API<br>- 직원 입사 추세 통계 API<br>- 테스트용 더미 데이터 생성 |

---

## 프로젝트 소개

**HR Bank**

Batch 기반 데이터 관리 기능을 포함한 **Open EMS (Employee Management System)** 입니다.  
기업의 직원 정보를 관리하고 직원 데이터 변경 이력 및 데이터 백업을 자동화하는 인사 관리 시스템입니다.

프로젝트 기간: 2026.03.10 ~ 03.20

### 배포 사이트

(현재는 배포를 내린 상태입니다)

### Swagger UI

(현재는 배포를 내린 상태입니다)

---

## 기술 스택

### Backend

[![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff)](#)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?&logoColor=fff)](#)

### Database

[![Postgres](https://img.shields.io/badge/Postgres-%23316192.svg?logo=postgresql&logoColor=white)](#)
[![H2](https://img.shields.io/badge/H2-4479A1?logo=h2&logoColor=fff)](#)

### Library

[![MapStruct](https://img.shields.io/badge/MapStruct-B0001C)](#)
[![Spring Scheduler](https://img.shields.io/badge/Spring%20Scheduler-6DB33F)](#)
[![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=173647)](#)
[![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?&logoColor=fff)](#)

### Infrastructure

[![Railway](https://img.shields.io/badge/Railway-0B0D0E?logo=railway&logoColor=white)](#)

### 협업 Tool

[![Git](https://img.shields.io/badge/Git-F05032?logo=git&logoColor=fff)](#)
[![GitHub](https://img.shields.io/badge/GitHub-%23121011.svg?logo=github&logoColor=white)](#)
[![Discord](https://img.shields.io/badge/Discord-%235865F2.svg?&logo=discord&logoColor=white)](#)

---
## ERD
<img width="1817" height="902" alt="HR_Bank_ERD_v4_가로형" src="https://github.com/user-attachments/assets/ed799bfd-70ec-4533-a3b3-2597eb9a9913" />

---
## 인프라 아키텍처 다이어그램
<img width="609" height="235" alt="HR_Bank_인프라 아키텍처 다이어그램" src="https://github.com/user-attachments/assets/400c0647-42a3-4e91-9d65-f1f8c619deb1" />

---
## 팀원별 구현 기능 상세
### 김성경
- **직원 관리 API**
  - 직원 등록 API: `POST /api/employees`
  - 직원 수정 API: `PATCH /api/employees/{id}`
  - 직원 삭제 API: `DELETE /api/employees/{id}`
  - 직원 단건 조회 API: `GET /api/employees/{id}`
- **직원 검색 API**
  - 직원 목록 조회 API: `GET /api/employees`
  - Cursor 기반 페이지네이션 적용

### 박나경
- **데이터 백업 관리 API**
  - 백업 목록 조회 API: `GET /api/backups` (Cursor 기반 페이지네이션)
  - 백업 수동 실행 API: `POST /api/backups`
  - 최신 백업 조회 API: `GET /api/backups/latest`
- **자동 백업 시스템**
  - 직원 데이터 CSV 파일 생성 및 저장

### 박승민
- **파일 관리 시스템**
  - 파일 다운로드 API: `GET /api/files/{id}/download`
  - 일반 파일(프로필 이미지) 및 백업 CSV 파일 저장
  - 파일 메타데이터(파일명, 형식, 크기) 분리 저장
- **자동 백업 시스템**
  - Spring Scheduler 기반 주기적 자동 백업

### 정수현
- **직원 정보 수정 이력 관리 API**
  - 이력 목록 조회 API: `GET /api/change-logs` (Cursor 기반 페이지네이션)
  - 이력 상세 조회 API: `GET /api/change-logs/{id}`
  - 이력 건수 조회 API: `GET /api/change-logs/count`
- **이력 추적 구조**
  - 직원 생성·수정·삭제 시 자동 이력 기록
  - 변경 전후 필드 단위 diff 저장

### 조성진
- **부서 관리 API**
  - 부서 등록 API: `POST /api/departments`
  - 부서 수정 API: `PATCH /api/departments/{id}`
  - 부서 삭제 API: `DELETE /api/departments/{id}`
  - 부서 단건 조회 API: `GET /api/departments/{id}`
  - 부서 목록 조회 API: `GET /api/departments` (Cursor 기반 페이지네이션)
  - 부서 이름 중복 검증 로직 구현
- **Railway 배포 설정**
  - Railway 기반 배포 환경 구축

### 최현호
- **대시보드 API**
  - 직원 수 조회 API: `GET /api/employees/count`
  - 직원 부서·상태별 분포 통계 API: `GET /api/employees/stats/distribution`
  - 직원 입사 추세 통계 API: `GET /api/employees/stats/trend`
- **테스트용 더미 데이터 생성** (`data.sql`)
---
## 파일 구조
```
src
┣ main
┃ ┣ java
┃ ┃ ┗ com.sprint.mission.hrbank
┃ ┃   ┣ domain
┃ ┃   ┃ ┣ backup
┃ ┃   ┃ ┣ baseentity
┃ ┃   ┃ ┣ changelog
┃ ┃   ┃ ┣ department
┃ ┃   ┃ ┣ employee
┃ ┃   ┃ ┗ file
┃ ┃   ┗ global
┃ ┃     ┣ exception
┃ ┃     ┗ response
┃ ┣ resources
┃ ┃ ┗ static
┗ test
```
---
# 프로젝트 회고록
[2팀_개별 개발 리포트](https://www.notion.so/3281d245f2c0802bb4abf027303793e4?source=copy_link)

[2팀_HR Bank_발표자료](https://drive.google.com/file/d/1Wn5lrkiN1FQ78C3X1i4kTU7DqzI7sKoH/view)  
[2팀_HR Bank 시연영상](https://drive.google.com/file/d/1VCHVTb_d0VHZntvvVvh-j-JZDEMLXhIC/view)
