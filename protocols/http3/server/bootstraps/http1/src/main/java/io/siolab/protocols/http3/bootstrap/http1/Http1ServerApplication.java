package io.siolab.protocols.http3.bootstrap.http1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.siolab.protocols.http3.endpoints")
public class Http1ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Http1ServerApplication.class, args);
    }
}
