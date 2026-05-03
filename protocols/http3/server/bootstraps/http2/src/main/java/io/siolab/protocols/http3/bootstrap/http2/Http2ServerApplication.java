package io.siolab.protocols.http3.bootstrap.http2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.siolab.protocols.http3.endpoints")
public class Http2ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Http2ServerApplication.class, args);
    }
}
