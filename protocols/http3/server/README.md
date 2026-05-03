# HTTP/3 Server

Spring Boot HTTP/1.1 ~ /3 테스트 서버

## Endpoints

- `GET /ping`: 짧은 JSON 응답
- `GET /payload?bytes=1024`: 지정 크기의 payload 응답
- `GET /stream?count=10`: SSE 스트림 응답
- `GET /actuator/health`: 헬스 체크

## Run

```bash
./gradlew :bootstraps:http1:bootRun
./gradlew :bootstraps:http2:bootRun
./gradlew :bootstraps:http3:bootRun
```

## Ports

- HTTP/1.1 bootstrap: TCP `8080`
- HTTP/2 bootstrap: TCP `8443`
- HTTP/3 bootstrap: UDP `8443`
