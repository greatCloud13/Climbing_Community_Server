##  프로젝트 소개

클라이밍 문제 리뷰&평가 커뮤니티 서비스 Climbing With 서버입니다.
---

##  기술 스택

### Backend
- **Framework**: Spring Boot 3.4.5
- **Language**: Java 21
- **ORM**: Spring Data JPA
- **Cache**: Redis 7
- **Message Queue**: RabbitMQ 3
- **Embedding Model**: bge-m3

### Database
- **RDBMS**: MySQL 8.0

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **API Test**: Swagger, Postman
- **Performance Test**: Apache JMeter

---

##  주요 기능

### 1. 클라이밍장 정보 제공
- 클라이밍장 관리자 지정을 통한 클라이밍장별 문제 레벨, 섹터, 세팅, 문제 등록 및 소개
### 2. 클라이밍 문제 리뷰 서비스
- 등록된 문제 리뷰 기능 제공
### 3. 게시글 시멘틱 검색 기능
- RAG 검색기능을 통한 단어 검색이 아닌 맥락 검색 기능 제공
---

##  실행 방법

### 1. 사전 준비
- Docker & Docker Compose 설치
- ollama BGE-M3 모델 설치

### 2. Docker 실행
```bash
# MySQL, Redis, RabbitMQ 실행
docker compose up -d
```

### 3. Ollama BGE-M3 로컬 실행
RAG 시스템을 정상적으로 동작시키려면 로컬(Ollama)에 `bge-m3` 모델이 실행 중이어야 합니다.

- **엔드포인트**: http://host.docker.internal:11434
- 해당 경로(Docker 호스트)를 통해 `bge-m3` 모델이 요청을 대기(Listening)하는 상태여야 합니다.

### 4. 접속 확인
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Redis Database**: http://localhost:8001/

---

##  API 문서

Swagger UI를 통해 모든 API를 확인하고 테스트할 수 있습니다.

자세한 API 명세는 Swagger 문서를 참고하세요.

---

##  브랜치 전략

- **Production** (운영 및 배포)
  - **설명**: AWS EC2 실서버 배포를 위한 최상위 브랜치입니다.
  - **규칙**: `Main` 브랜치로부터의 PR을 통해서만 반영(Push)이 가능합니다.
- **Main** (개발)
  - **설명**: 신규 기능 개발, 버그 수정, 리팩토링이 이루어지는 중심 브랜치입니다.
  - **규칙**: 해당 브랜치를 `Fork`하여 개별 작업 후 PR을 보냅니다.

---

##  향후 계획
- [ ] RAG 클라이밍장, 태그 구분을 통한 검색 성능 강화
- [ ] Prometheus + Grafana 모니터링
