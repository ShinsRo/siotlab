package io.siolab.protocols.http3.bootstrap.http3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.siolab.protocols.http3.endpoints")
public class Http3ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Http3ServerApplication.class, args);
    }
}
