##  프로젝트 소개

클라이밍 문제 리뷰&평가 커뮤니티 서비스 Climbing With 서버입니다.
---

##  기술 스택

### Backend
- **Framework**: Spring Boot 3.4.5
- **Language**: Java 21
- **ORM**: Spring Data JPA (+ QueryDSL)
- **Auth**: Spring Security, JWT
- **Cache**: Redis 7
- **Message Queue**: RabbitMQ 3
- **Embedding Model**: bge-m3 (Ollama, LangChain4j)

### Database
- **RDBMS**: MySQL 8.0
- **Vector Store**: Redis Stack (RediSearch)

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
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

##  아키텍처

```
                        ┌─────────────┐
   Client ────HTTP────▶ │  Spring Boot │
                        │   (app)      │
                        └──────┬──────┘
                  ┌────────────┼────────────┐
                  ▼            ▼             ▼
             ┌────────┐  ┌──────────┐  ┌──────────┐
             │  MySQL │  │ RabbitMQ │  │  Redis   │
             │ (RDB)  │  │  (Queue) │  │ (Cache)  │
             └────────┘  └────┬─────┘  └──────────┘
                               │ 비동기 처리
                               ▼
                        ┌──────────────┐
                        │ Embedding    │
                        │ Consumer     │──▶ Ollama(bge-m3)
                        └──────┬───────┘
                               ▼
                        ┌──────────────┐
                        │ Redis Vector │
                        │ Store (RAG)  │
                        └──────────────┘
```

- **게시글 등록** → RabbitMQ에 임베딩 요청 메시지 발행 → Consumer가 비동기로 bge-m3 임베딩 생성 → Redis Vector Store에 저장 → 게시글 시멘틱 검색에 활용
- **왜 임베딩을 비동기로 처리하는 이유**: 게시글 등록 요청 시점에 임베딩 모델 호출까지 동기로 묶으면 API 응답 지연 및 임베딩 서버 장애가 게시글 등록 자체를 막게 됩니다. RabbitMQ로 분리해 게시글 등록은 즉시 응답하고, 임베딩 실패는 별도 재시도(최대 3회) 후 DLQ로 격리해 게시글 CRUD와 검색 기능의 장애를 분리했습니다.

---

##  실행 방법 (로컬 개발)

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

> 로컬 개발 환경(`main` 브랜치)은 `application.yaml`에 기본값이 하드코딩되어 있어 별도 `.env` 설정 없이 바로 실행됩니다. 운영 환경 설정은 아래 [배포](#-배포-production) 항목을 참고하세요.

### 5. 테스트 실행
```bash
./gradlew test
```
`src/test/resources/application.yaml`이 H2 인메모리 DB를 사용하도록 오버라이드하고, 벡터 검색 관련 컨텍스트 로딩 테스트는 로컬에 Redis Stack이 실행중이여야 통과합니다 (CI에서는 `redis/redis-stack-server` 서비스 컨테이너로 대체).

---

##  API 문서

Swagger UI를 통해 모든 API를 확인하고 테스트할 수 있습니다.

자세한 API 명세는 Swagger 문서를 참고하세요.

| 리소스 | 설명 |
|---|---|
| `/api/auth` | 회원가입 / 로그인 / 탈퇴 |
| `/api/gym`, `/api/sector`, `/api/level`, `/api/setting` | 클라이밍장·섹터·레벨·세팅 조회 및 관리자 등록 |
| `/api/problem`, `/api/review` | 문제 등록 및 리뷰 |
| `/api/post` | 게시글 CRUD 및 시멘틱 검색(`/api/post/search`) |
| `/api/clearRecord` | 완등 기록 등록/통계 |
| `/api/files` | 파일 업로드 |
| `/api/user` | 사용자 조회 및 클라이밍장 매니저 지정 |

읽기(GET)는 비로그인 사용자도 열람 가능하며, 생성/수정/삭제는 로그인이 필요합니다. `/admin/**`은 `ROLE_ADMIN`만 접근 가능합니다.

---

##  브랜치 전략

- **Production** (운영 및 배포)
  - **설명**: AWS EC2 실서버 배포를 위한 최상위 브랜치입니다.
  - **규칙**: `Main` 브랜치로부터의 PR을 통해서만 반영(Push)이 가능합니다.
  - `main`과 달리 시크릿을 환경변수로 외부화한 `docker-compose.prod.yml`을 별도로 가집니다.
- **Main** (개발)
  - **설명**: 신규 기능 개발, 버그 수정, 리팩토링이 이루어지는 중심 브랜치입니다.
  - **규칙**: 해당 브랜치를 `Fork`하여 개별 작업 후 PR을 보냅니다.

---

##  CI/CD 파이프라인

GitHub Actions(`.github/workflows/deploy.yml`) 기준으로 브랜치별 역할이 다릅니다.

| 트리거 | 실행되는 잡 |
|---|---|
| `main`으로 push / PR | `test` (Redis 서비스 컨테이너와 함께 `./gradlew test`) |
| `production`으로 push | `test` → `build`(Docker 이미지 빌드 & Docker Hub push) → `deploy`(EC2 반영) |

`deploy` 잡은 다음 순서로 동작합니다.
1. `docker-compose.prod.yml`을 EC2 서버(`~/app`)로 복사
2. GitHub Secrets 값으로 `.env` 파일을 EC2에 생성 (레포에는 절대 커밋되지 않음)
3. Docker Hub에서 최신 이미지 pull
4. `docker-compose -f docker-compose.prod.yml up -d`로 재기동

---

##  배포 (Production)

`production` 브랜치는 EC2에 실제 서비스가 배포되는 환경으로, 아래 시크릿 값들을 컨테이너 환경변수로 주입받아 `application.yaml`의 개발용 기본값을 덮어씁니다 (Spring Boot의 환경변수 우선순위 활용, yaml 자체는 수정하지 않음).

### 필요한 GitHub Secrets

| 이름 | 용도 |
|---|---|
| `DOCKER_USERNAME` / `DOCKER_PASSWORD` | Docker Hub 로그인 및 이미지 push/pull |
| `EC2_HOST` / `EC2_USERNAME` / `EC2_KEY` | EC2 SSH 접속 |
| `DB_PASSWORD` | MySQL root 비밀번호 |
| `RABBITMQ_PASSWORD` (`RABBITMQ_USERNAME`은 선택) | RabbitMQ 계정 |
| `JWT_SECRET` | JWT 서명 키 |

### 로컬에서 운영 설정을 재현하고 싶다면
```bash
cp .env.example .env   # 값 채운 뒤
docker compose -f docker-compose.prod.yml --env-file .env up -d
```

> ⚠️ EC2는 비용 절감을 위해 상시 가동하지 않습니다. 배포 전 인스턴스가 켜져 있는지 확인이 필요합니다.

---

##  향후 계획
- [ ] RAG 클라이밍장, 태그 구분을 통한 검색 성능 강화
- [ ] Prometheus + Grafana 모니터링