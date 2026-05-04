package io.siolab.protocols.http3.bootstrap.http2;

import io.siolab.protocols.http3.endpoints.BenchmarkController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
    Http2ServerApplication.class,
    BenchmarkController.class
})
public class Http2ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Http2ServerApplication.class, args);
    }
}
