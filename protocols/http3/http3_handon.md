# http3 핸즈온

## Spring 과 http3 조합에서의 주의 사항

일반적으로 edge 레벨에서 http3 를 종료하므로, 이 프로젝트는 참고사항!

- http/3 은 TLS 1.3 이상을 요구하므로, TLS 설정에 주의
- http/3 은 UDP 443 포트를 열어야 함
- 네티 버전에 따라 지원되는 http/3 버전이 다를 수 있음
- 톰캣은 현 일시 기준 지원하지 않음
- http/3 은 서버와 클라이언트 모두에서 지원해야 함

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