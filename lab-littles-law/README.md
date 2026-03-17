# Throughput / Concurrency Lab

Little’s Law 기반으로 시스템 처리량, 응답시간, 동시처리량(in-flight)의 관계를 실험적으로 관찰하기 위한 간단한 실험 환경이다.  
애플리케이션 계층과 DB 계층의 부하를 분리 측정하고, 각 변수 변화가 TPS와 latency에 미치는 영향을 비교하는 것을 목표로 한다.

---

## 목적

다음 질문에 실험적으로 답하는 것을 목표로 한다.

- Little’s Law (`L = λW`)가 실제 시스템 측정값과 어떻게 대응되는가
- TPS, 응답시간, 동시처리량(in-flight)의 관계
- 스레드 수, 커넥션 풀, 큐 크기 등의 주요 변수 변화가 처리량에 미치는 영향
- 애플리케이션 병목과 DB 병목을 어떻게 구분할 수 있는가
- 버퍼링(큐)이 순간 트래픽을 얼마나 흡수할 수 있는가

---

## 핵심 관측 지표

모든 실험에서 다음 지표를 공통적으로 수집한다.

- TPS (completed throughput)
- 평균 응답시간
- p95 / p99 latency
- in-flight 요청 수
- 에러율 / reject 수

추가적으로 다음 내부 상태를 관찰한다.

- active worker / thread 수
- DB connection pool 상태
- queue depth
- queue wait time

---

## 실험 변수

실험은 **한 번에 하나의 변수만 변경하고 나머지는 고정**하는 방식으로 수행한다.

### 애플리케이션 계층 변수
- worker / thread 수
- 애플리케이션 동시 처리 제한값
- 요청당 처리시간
- 내부 queue 크기
- queue consumer 수

### DB 계층 변수
- connection pool size
- query latency
- 요청당 DB 작업 수
- row contention (hot row 등)

---

## 실험 구조

실험 환경은 세 개의 구성요소로 이루어진다.

### Load Generator
- k6를 사용하여 트래픽 생성
- TPS, burst, duration, ramp-up 제어

### Scenario Runner
- Python 스크립트
- 실험 시나리오 실행
- 변수 설정 및 실험 자동화

### Target System
- Spring 애플리케이션
- MySQL
- 필요 시 내부 queue 및 worker

---

## 실험 단계

### App-only
DB를 배제하고 애플리케이션 처리 한계를 측정한다.

목적:
- 웹 계층 및 worker 처리 능력 측정
- thread / concurrency 제한 효과 확인
- 순수 애플리케이션 TPS 상한 확인

---

### DB-only 성격 실험
애플리케이션 로직을 최소화하고 DB 작업의 처리 한계를 측정한다.

목적:
- DB QPS 한계 측정
- connection pool 영향 확인
- query latency 및 lock 영향 관찰

---

### Integrated
애플리케이션과 DB를 함께 연결하여 전체 시스템 동작을 검증한다.

목적:
- 실제 end-to-end TPS 측정
- 병목 지점 확인
- 분리 실험 결과와 통합 결과 비교

---

## 실험 방식

- 변수 하나만 변경하고 나머지는 고정
- 동일한 부하 조건에서 반복 실험 수행
- Little’s Law 관점에서 TPS, 응답시간, in-flight 관계 비교
- App-only, DB-only, Integrated 결과를 함께 분석

---

## 기대 결과

- 애플리케이션 병목과 DB 병목을 구분할 수 있는 기준 확보
- 주요 시스템 변수(thread, pool, queue 등)의 TPS 민감도 파악
- queue 및 worker sizing에 대한 경험적 기준 확보
- 이론적 계산(Little’s Law)과 실제 시스템 거동 차이 확인

---

## TODO

### 실험 환경
- [ ] Spring Boot 기반 실험용 애플리케이션 생성
- [ ] MySQL docker 환경 구성
- [ ] docker-compose로 전체 실험 환경 구성

### 부하 생성
- [ ] k6 스크립트 작성 (기본 TPS 테스트)
- [ ] k6 burst / ramp-up 시나리오 작성
- [ ] Python 기반 scenario runner 작성
- [ ] Python 스크립트로 실험 변수 자동 변경 및 반복 실행

### 실험 모드 구현
- [ ] NOP 모드 (DB 없이 요청 처리)
- [ ] Sync DB 모드
- [ ] Buffered 모드 (internal queue)
- [ ] Queue reject 모드 (bounded queue)

### 실험 변수 제어
- [ ] worker/thread 수 설정 가능
- [ ] app concurrency limit 설정
- [ ] queue size 설정
- [ ] queue consumer 수 설정
- [ ] DB connection pool size 설정
- [ ] 요청 처리시간(cost) 시뮬레이션

### 메트릭 수집
- [ ] TPS 측정
- [ ] latency (avg/p95/p99) 측정
- [ ] in-flight 요청 수 측정
- [ ] queue depth 측정
- [ ] worker active count 측정
- [ ] DB pool 상태 측정

### Observability
- [ ] OpenTelemetry SDK 적용
- [ ] OTEL exporter 설정
- [ ] OTEL collector docker 구성
- [ ] Jaeger 또는 Tempo 연동
- [ ] Prometheus + Grafana 메트릭 대시보드 구성

### 실험 자동화
- [ ] Python 스크립트로 실험 결과 수집
- [ ] 결과 CSV 또는 JSON 저장
- [ ] 변수별 실험 결과 비교 스크립트 작성
- [ ] 그래프 생성 (TPS vs latency / inflight)