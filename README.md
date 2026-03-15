# HR Bank

(팀 협업 문서 링크 게시)

---

# 팀원 구성

김성경 ([개인 Github 링크](https://github.com/conradrado))  
박나경 ([개인 Github 링크](https://github.com/parkngg))  
최현호 ([개인 Github 링크](https://github.com/CHH01))  
정수현 ([개인 Github 링크](https://github.com/JeongSooHyeon))
조성진 ([개인 Github 링크](https://github.com/Amperisk9))
박승민 ([개인 Github 링크](https://github.com/raonPsm))
---

# 프로젝트 소개

**HR Bank**

Batch 기반 데이터 관리 기능을 포함한 **Open EMS (Employee Management System)** 입니다.  
기업의 직원 정보를 관리하고 직원 데이터 변경 이력 및 데이터 백업을 자동화하는 인사 관리 시스템입니다.

프로젝트 기간: 2026.03.10 ~ 2026.03.20

---

# 기술 스택

### Backend
- Java
- Spring Boot
- Spring Data JPA

### Database
- PostgreSQL
- H2 (Local Development)

### Library
- MapStruct
- Spring Scheduler
- SpringDoc OpenAPI (Swagger)

### Infrastructure
- Railway

### 협업 Tool
- Git & Github
- Discord

---

# 팀원별 구현 기능 상세

## 김성경

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

### 직원 관리 API

직원 정보를 생성, 수정, 삭제하는 API 구현

- 직원 등록 API
- 직원 수정 API
- 직원 삭제 API
- 직원 상세 조회 API

### 직원 검색 API

다양한 조건 기반 직원 검색 기능 구현

검색 조건

- 이름
- 이메일
- 부서
- 직함
- 사원번호
- 입사일 범위
- 상태

Cursor 기반 페이지네이션 적용

---

## 박승민

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

### 부서 관리 API

부서 관리 기능 구현

- 부서 등록 API
- 부서 수정 API
- 부서 삭제 API
- 부서 목록 조회 API

부서 이름 중복 검증 로직 구현

---

## 박나경

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

### 파일 관리 시스템

파일 메타데이터와 실제 파일을 분리 저장

메타 정보

- 파일명
- 파일 형식
- 파일 크기

저장 구조


---

## 최현호

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

### 파일 관리 시스템

파일 메타데이터와 실제 파일을 분리 저장

메타 정보

- 파일명
- 파일 형식
- 파일 크기

저장 구조

---

## 정수

(자신이 개발한 기능에 대한 사진이나 gif 파일 첨부)

### 파일 관리 시스템

파일 메타데이터와 실제 파일을 분리 저장

메타 정보

- 파일명
- 파일 형식
- 파일 크기

저장 구조

# 파일 구조


src
┣ main
┃ ┣ java
┃ ┃ ┣ com
┃ ┃ ┃ ┣ hrbank
┃ ┃ ┃ ┃ ┣ controller
┃ ┃ ┃ ┃ ┃ ┣ EmployeeController.java
┃ ┃ ┃ ┃ ┃ ┣ DepartmentController.java
┃ ┃ ┃ ┃ ┃ ┗ BackupController.java
┃ ┃ ┃ ┃ ┣ service
┃ ┃ ┃ ┃ ┣ repository
┃ ┃ ┃ ┃ ┣ entity
┃ ┃ ┃ ┃ ┣ dto
┃ ┃ ┃ ┃ ┗ config
┃ ┣ resources
┃ ┃ ┣ application.yml
┃ ┃ ┗ static
┗ test


---

# API 문서

Swagger UI


/swagger-ui/index.html


---

# 구현 시스템

(프로젝트 실행 화면 또는 데모 링크)

---

# 프로젝트 회고록

(프로젝트 발표 자료 또는 회고 링크)

프로젝트를 통해 다음을 학습했습니다.

- Spring Boot 기반 REST API 설계
- JPA 기반 데이터 관리
- Cursor 기반 페이지네이션
- 파일 관리 시스템 구현
- Batch 기반 데이터 백업 시스템 설계
- 협업 기반 Git Workflow

---

# Author

Backend Developer  
김성경

