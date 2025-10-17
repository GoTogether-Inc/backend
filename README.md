# GoTogether

이 문서는 Spring Boot 기반의 이벤트 티켓팅 플랫폼 백엔드 서버의 개발 및 배포 환경을 설명합니다.

---

## Table of Contents

* 시스템 개요
* 주요 기능
* 코드베이스 구조
* 로컬 개발 가이드
* Docker 배포
* 커밋 컨벤션
* 기술 스택
* API 응답 구조

---

## System Overview

| 구성요소 | 설명                                    |
|---------|---------------------------------------|
| 백엔드 서버 | Spring Boot 3.2.11 (Gradle) + Java 17 |
| 데이터베이스 | MySQL 8.0+                            |
| 인증 | JWT + OAuth2 (Google, Kakao)          |
| 캐싱 | Redis                                 |
| 파일 저장소 | AWS S3                                |
| 스케줄링 | Quartz Scheduler                      |
| 모니터링 | Prometheus + Micrometer               |
| 문서화 | Swagger (SpringDoc OpenAPI)           |
| 테스트 커버리지 | JaCoCo                                |

---

## Features

### Main Service Flow

```
사용자 인증 → 이벤트 생성/조회 → 티켓 예매 → QR코드 발급 → 입장 관리
```

---

### 인증 및 보안
- OAuth2 소셜 로그인 (Google, Kakao)
- JWT 토큰 기반 인증
- Spring Security 적용

### 이벤트 관리
- 이벤트 관리
- 태그별 이벤트 목록 (최신/인기/마감임박)
- 키워드 검색 기능
- 카테고리별 이벤트 필터

### 티켓 예매 시스템
- 티켓 옵션 관리 (사용자 질문, 응답)
- 실시간 재고 관리
- 주문 및 결제 처리
- QR코드 기반 입장 관리

### 추가 기능
- 북마크 기능
- 이메일 예약 알림
- SMS 인증
- Excel 데이터 내보내기
- 스케줄링 (Quartz)

---

## Codebase Structure

```
src/main/java/com/gotogether/
├── domain/
│   ├── user/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── converter/
│   ├── event/
│   ├── hostchannel/
│   ├── ticket/
│   ├── order/
│   ├── bookmark/
│   ├── hashtag/
│   ├── ticketoption/
│   ├── ticketoptionanswer/
│   ├── ticketoptionassignment/
│   ├── ticketqrcode/
│   ├── reservationemail/
│   ├── term/
│   ├── referencelink/
├── global/
│   ├── config/
│   ├── oauth/
│   ├── scheduler/
│   ├── util/
│   ├── annotation/
│   ├── apipayload/
│   ├── aspect/
│   ├── common/
│   ├── constants/
│   ├── filter/
│   └── interceptor/
└── GoTogetherApplication.java   
```

### Package Structure

- `controller/`: REST API 컨트롤러
- `service/`: 비즈니스 로직
- `repository/`: 데이터 접근 계층
- `entity/`: JPA 엔티티
- `dto/`: 데이터 전송 객체
- `converter/`: DTO 변환 유틸리티

---

## Local Development Guide

### 1. Run with Gradle

```bash
cd backend
./gradlew clean build     # 의존성 설치 및 빌드
./gradlew bootRun         # Spring Boot 서버 실행
```

실행 후 API는 `http://localhost:8080`에서 접근 가능합니다.

### 2. API Documentation

Swagger UI를 통해 API 문서를 확인할 수 있습니다:
- URL: `http://localhost:8080/swagger-ui/index.html`

---

## Docker Deployment

### Docker Image Build and Deployment

```bash
# Jib을 사용한 Docker 이미지 빌드
./gradlew jib

# 또는 Docker 빌드
docker build -t gotogether:latest .
```

### Container Execution

```bash
# Docker Compose로 실행 (설정 파일 필요)
docker-compose up -d

# 단일 컨테이너 실행
docker run -p 8080:8080 gotogether:latest
```

---

## Commit Convention

```
<type>: <description>

[optional body]

[optional footer]
```

### Type (필수)
| Type | Description | 
|------|----------|
| `feat` | 새로운 기능 추가 | 
| `fix` | 버그 수정 | 
| `docs` | 문서 수정 |
| `style` | 코드 스타일 변경 |
| `refactor` | 코드 리팩토링 |
| `test` | 테스트 추가 또는 수정 |
| `chore` | 빌드 또는 기타 작업 |

### Description (필수)
- 마침표(.) 사용하지 않음
- 50자 이내로 간결하게

---

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.11
- **Language**: Java 17
- **Build Tool**: Gradle
- **Database**: MySQL
- **ORM**: Spring Data JPA + QueryDSL
- **Security**: Spring Security + JWT
- **OAuth2**: Spring OAuth2 Client

### Infrastructure & Tools
- **Caching**: Redis
- **File Storage**: AWS S3
- **Scheduling**: Quartz Scheduler
- **Monitoring**: Prometheus + Micrometer
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Testing**: JUnit 5 + Mockito
- **Code Coverage**: JaCoCo
- **Container**: Docker + Jib

### External Services
- **SMS**: CoolSMS
- **Email**: JavaMailSender
- **QR Code**: ZXing
- **Excel**: Apache POI

---

## API Response Structure

### Success Response
```json
{
  "isSuccess": true,
  "code": "200",
  "message": "OK",
  "result": {
    // 실제 데이터
  }
}
```

### Error Response
```json
{
  "isSuccess": false,
  "code": "COMMON4000",
  "message": "잘못된 요청입니다.",
  "result": null
  }
  
  ```

---

### Error Code Definition
```java
@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 공통 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON5000", "서버 에러. 관리자에게 문의하세요."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON4000", "잘못된 요청입니다."),
    
    // 사용자 관련 에러
    _USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자가 없습니다."),
    _USER_EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4003", "이미 등록된 이메일입니다."),
    
    // 이벤트 관련 에러
    _EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT4001", "이벤트가 없습니다."),
    _EVENT_DELETE_FAILED_ORDERS_EXIST(HttpStatus.BAD_REQUEST, "EVENT4002", "구매자가 있는 이벤트는 삭제할 수 없습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    
    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
            .message(message)
            .code(code)
            .isSuccess(false)
            .httpStatus(httpStatus)
            .build();
    }
}
```

### Usage Example
```java
// 예외 발생
throw new GeneralException(ErrorStatus._USER_NOT_FOUND);

// 응답 예시
{
  "isSuccess": false,
  "code": "USER4001",
  "message": "사용자가 없습니다.",
  "result": null
}
```

