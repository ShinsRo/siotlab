package io.siolab.protocols.http3.bootstrap.http3;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import reactor.netty.http.Http3SslContextSpec;
import reactor.netty.http.HttpProtocol;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class Http3NettyCustomizer {
    // 로컬 테스트 용도로만  사용할 것
    private static final String KEY_STORE = "certs/http3.p12";
    private static final char[] KEY_STORE_PASSWORD = "changeit".toCharArray();

    @Bean
    WebServerFactoryCustomizer<NettyReactiveWebServerFactory> http3ServerCustomizer() {
        return factory -> factory.addServerCustomizers(server -> server
            .protocol(HttpProtocol.HTTP3)
            .secure(ssl -> ssl.sslContext(http3SslContextSpec()))
        );
    }

    private static Http3SslContextSpec http3SslContextSpec() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream input = new ClassPathResource(KEY_STORE).getInputStream()) {
                keyStore.load(input, KEY_STORE_PASSWORD);
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm()
            );
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD);

            return Http3SslContextSpec.forServer(keyManagerFactory, String.valueOf(KEY_STORE_PASSWORD));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure HTTP/3 SSL context", e);
        }
    }
}
