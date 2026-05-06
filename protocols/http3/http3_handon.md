# http3 핸즈온

## Spring 과 http3 조합에서의 주의 사항

일반적으로 edge 레벨에서 http3 를 종료하므로, 이 프로젝트는 참고사항!

- http/3 은 TLS 1.3 이상을 요구하므로, TLS 설정에 주의
- http/3 은 UDP 443 포트를 열어야 함
- 네티 버전에 따라 지원되는 http/3 버전이 다를 수 있음
- 네티 4.2 부터 인큐베이팅 아티팩트가 아님 (부트 버전은 4.0.6 이상부터)
  - 왠지는 모르겠지만 네티의 DEFAULT_MAX_STREAMS_BIDIRECTIONAL 는 0L 이라서
  - 커스터마이저로 http3 바이디렉셔널 스트림 수 지정하지 않으면 스트림 생성이 불가해 curl 이나 oha 에서 에러가 남
  - 서버로부터 응답받을 initial_max_streams_bidi 가 있어야 됌
    - remote 와 local 로 스콥 나눠 지정할 수 있는데
      - remote: 상대 노드에서 bidi 생성한거임
      - local: 내 노드가 bidi 생성한거임
- 톰캣은 현 일시 기준 지원하지 않음
- http/3 은 서버와 클라이언트 모두에서 지원해야 함
- 또한 UDP 는 같은 포트번호를 갖는 서버가 2개 이상 뜰 수 있으므로, 주의해야함.

- bootstraps/http1
    - server.port=8080
    - 별도 커스터마이저 불필요

- bootstraps/http2
    - server.port=8443
    - server.http2.enabled=true
    - TLS 설정 필요

- bootstraps/http3
    - server.port=8443
    - WebServerFactoryCustomizer 필요
    - HttpProtocol.HTTP3 지정
    - Http3SslContextSpec 필요
    - netty-codec-native-quic 필요