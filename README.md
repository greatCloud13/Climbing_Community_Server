#  도서 리뷰 플랫폼

> Redis 캐싱과 RabbitMQ 비동기 처리를 활용한 고성능 도서 검색/리뷰 서비스

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3-orange.svg)](https://www.rabbitmq.com/)


##  목차

- [프로젝트 소개](#-프로젝트-소개)
- [핵심 성과](#-핵심-성과)
- [기술 스택](#-기술-스택)
- [아키텍처](#-아키텍처)
- [주요 기능](#-주요-기능)
- [성능 개선](#-성능-개선)
- [실행 방법](#-실행-방법)
- [API 문서](#-api-문서)

---

##  프로젝트 소개

클라이밍 문제 리뷰&평가 커뮤니티 서비스 Climbing With의 서버입니다.
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

### 3. 접속 확인
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Redis Database**: http://localhost:8001/

---

##  API 문서

Swagger UI를 통해 모든 API를 확인하고 테스트할 수 있습니다.

자세한 API 명세는 Swagger 문서를 참고하세요.

---

##  향후 계획
- [ ] RAG 클라이밍장, 태그 구분을 통한 검색 성능 강화
- [ ] Prometheus + Grafana 모니터링
