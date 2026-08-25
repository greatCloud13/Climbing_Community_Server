# JWT 인증 시스템 1.0 배포 전 개선 — Phase 1 보고서

- 브랜치: `FIX-JWT-Secret-환경변수-이관` (base: `production`)
- 대상 PR: `FIX-JWT-Secret-환경변수-이관` → `production`
- 범위: 1.0 배포 전 필수 항목 중 **#1 Secret Key 환경변수 이관**, **#2 CORS 외부 설정화**
- 사전 리뷰: production 브랜치는 이번 작업 착수 시점 기준 main과 완전히 동일함을 확인 (`git diff main production` 결과 없음)

---

## 1. 완료된 작업

### 1-1. Secret Key 환경변수 이관

**착수 전 파악한 문제**: `application.yaml`에 JWT 서명 키가 평문 하드코딩, `TODO: [PRODUCTION] 환경변수로 변경 필요` 주석이 남아 있어 미조치 상태로 보였음.

**작업 중 확인된 사실**: 배포 파이프라인 전체(`deploy.yml`, `docker-compose.prod.yml`, `README.md`)를 확인한 결과, `JWT_SECRET` 환경변수 주입 경로(GitHub Actions Secrets → EC2 `.env` → `docker-compose.prod.yml` → Spring Boot 환경변수 우선순위)는 **이미 완전히 구축되어 있었음**. 즉 실제 배포에서는 처음부터 yaml의 하드코딩 값이 쓰이지 않고 있었고, 원래 리뷰에서 매긴 위험도는 실제보다 높게 평가되어 있었음.

실질적으로 남아있던 문제는 (a) 이미 해결된 사안을 미완료로 오인시키는 낡은 TODO 주석, (b) `docker-compose.prod.yml`/`README.md`가 안내하는 `.env.example` 파일이 실제로는 존재하지 않았던 점.

**변경 내용**
- [`application.yaml`](../../src/main/resources/application.yaml:34) — `secret: your-secret-key-...` → `secret: ${JWT_SECRET:your-secret-key-...}` 명시적 placeholder로 변경(암묵적 오버라이드를 코드에서 바로 드러나도록), 낡은 TODO 제거
- [`test/application.yaml`](../../src/test/resources/application.yaml:28) — 동일 TODO 제거(테스트는 격리 실행을 위해 고정값 유지, CI가 JWT_SECRET을 설정하지 않으므로 변경 불필요)
- [`.env.example`](../../.env.example) 신규 생성 — `DB_PASSWORD`, `RABBITMQ_USERNAME/PASSWORD`, `JWT_SECRET`, `DOCKER_USERNAME` 항목 문서화

### 1-2. CORS 허용 출처 외부 설정화

**착수 전 파악한 문제**: `SecurityConfig.corsConfigurationSource()`에 `"http://localhost:60869"`(Flutter 디버그 포트)가 하드코딩, `TODO: [PRODUCTION] 배포 시 실제 프론트엔드 도메인으로 변경 필요함` 주석 존재.

**설계 판단**: 현재 클라이언트는 순수 네이티브 앱(Flutter iOS/Android)이라 CORS 자체가 실질적 영향은 없으나(CORS는 브라우저가 강제하는 정책), 추후 웹 클라이언트(관리자 대시보드 등) 추가 가능성이 있다고 확인되어 — 도메인이 정해지지 않은 지금 값을 코드에 박지 않고 배포 설정만으로 나중에 바꿀 수 있도록 외부화.

**변경 내용**
- [`SecurityConfig.java`](../../src/main/java/com/project/greatcloud13/ClimbingWith/config/SecurityConfig.java) — `@Value("${cors.allowed-origins}") private String[] allowedOrigins;` 추가, 하드코딩된 origin을 이 필드로 교체. 콤마 구분 문자열 하나로 다중 출처도 코드 변경 없이 등록 가능.
- [`application.yaml`](../../src/main/resources/application.yaml:39) — `cors.allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:60869}` 추가, 로컬 기본값 유지.
- [`test/application.yaml`](../../src/test/resources/application.yaml:33) — `cors.allowed-origins: http://localhost:3000` 고정값 추가(누락 시 `SecurityConfig` 빈 생성 실패로 전체 테스트 컨텍스트 로딩이 깨짐을 확인하고 추가).
- [`docker-compose.prod.yml`](../../docker-compose.prod.yml:70-71) — `CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS:-}` 추가. `DB_PASSWORD`/`JWT_SECRET`과 달리 **선택값**(`:-`, 필수 아님)으로 설정해 도메인 미정 상태에서도 배포가 막히지 않도록 함. 미설정 시 허용 출처 0개가 되어 브라우저 기반 클라이언트만 차단되고 네이티브 앱 트래픽에는 영향 없음.
- [`.env.example`](../../.env.example) — `CORS_ALLOWED_ORIGINS` 항목 추가, 선택 사항임을 명시.

## 2. 검증

- `./gradlew compileJava compileTestJava` → BUILD SUCCESSFUL
- `./gradlew test` (전체 테스트, 두 작업 각각 및 최종 통합 상태에서 총 3회 실행) → BUILD SUCCESSFUL, 실패 없음

## 3. 변경 파일 목록

| 파일 | 내용 |
|---|---|
| `src/main/resources/application.yaml` | jwt.secret / cors.allowed-origins 외부화 |
| `src/test/resources/application.yaml` | 테스트용 cors.allowed-origins 고정값 추가, 낡은 TODO 정리 |
| `src/main/java/.../config/SecurityConfig.java` | CORS origin을 `@Value` 필드로 교체 |
| `docker-compose.prod.yml` | `CORS_ALLOWED_ORIGINS` 선택적 환경변수 추가 |
| `.env.example` | 신규 생성, JWT_SECRET/CORS_ALLOWED_ORIGINS 등 문서화 |

---

## 4. 후속 작업 (Follow-up)

### 🔴 필수 (1.0 배포 전, 이번 PR 범위 밖 — 다음 단계로 진행 예정)

3. **Refresh Token + Reissue 메커니즘 도입** — Access Token 30분/Refresh Token 14일 이원화, 기존 Redis(vector, redis-stack) 재사용해 `refresh:{username}` 키에 TTL 저장, `POST /api/auth/reissue`(Rotation + 재사용 감지)와 `POST /api/auth/logout` 신규 구현. `spring-boot-starter-data-redis` 의존성 추가 필요.
4. **인증 실패 응답 명확화** — 커스텀 `AuthenticationEntryPoint` 추가, 프로젝트 기존 `ApiResult`/`ErrorCode` 포맷으로 401 JSON 응답.
5. **예외 세분화** — `JwtTokenProvider.validateToken()`이 `ExpiredJwtException`/`JwtException`을 구분해 던지도록 변경, `JwtAuthenticationFilter`에서 구분 처리 후 4번의 EntryPoint에 사유 전달.

> 3~5번은 서로 연결된 설계라 함께 진행 예정이며, 별도 브랜치·PR·보고서로 분리할지 이번 PR에 이어 작업할지는 착수 시 확인 필요.

### 🟡 권장

6. 로그인 Rate Limiting / 계정 잠금 (brute-force 방어 전무)
7. `application.yaml:12`의 DB 비밀번호(`p@ssword`) 평문 하드코딩 — JWT Secret과 동일한 패턴의 문제이나 이번 작업 범위에는 포함하지 않음. 로컬 개발용 `docker-compose.yml`에도 동일 값이 중복 노출되어 있어 별도 검토 권장.
8. GitHub Secrets에 등록된 `JWT_SECRET` 실제 값이 `application.yaml`의 fallback 문자열(`your-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm`)과 다른, 충분히 무작위한 값인지 확인 필요 — 저장소 접근 권한 밖이라 이번 작업에서는 확인 불가, 배포 담당자 확인 필요.
9. 로깅 점검 — `JwtTokenProvider`의 사용자 식별정보 로그 레벨 재검토.
10. 미사용 `claims` 빈 `HashMap` 정리 — 3~5번 작업 중 실제 클레임(`type: access/refresh`)으로 대체될 예정이라 자연스럽게 해소됨.

### ⚪ 선택

11. HTTPS 강제 (인프라 레벨)
12. `jti` 클레임 도입 — 개별 토큰 단위 무효화 대비
13. 매 요청 DB 재조회(`CustomUserDetailsService`) 방식의 트래픽 증가 시 캐싱 검토

### 📌 이번 작업 중 발견했으나 처리하지 않은 항목

- `git stash@{0}`(작업 시작 전 `main`에서 가져온 WIP)에 CORS 외부화와 무관한 변경이 함께 있었음: `GET /api/problem/*/detail`을 `authenticated()`로 지정하는 인가 정책 수정(로그인 사용자의 시도 횟수 노출 관련). CORS 작업과 무관해 이번 PR에는 포함하지 않았고 stash에 보존 중. 필요 시 별도로 검토 요망 (`git stash show -p stash@{0}`).
- `LoginResponse`의 `token` 필드명을 3번(Refresh Token) 작업에서 `accessToken`으로 바꿀지 여부 — 클라이언트(Flutter) 동시 수정이 필요해 아직 결정되지 않음. 3번 착수 전 확인 필요.
