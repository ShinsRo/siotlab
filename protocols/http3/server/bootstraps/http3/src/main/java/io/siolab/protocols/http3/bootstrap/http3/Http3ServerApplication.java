package io.siolab.protocols.http3.bootstrap.http3;

import io.siolab.protocols.http3.endpoints.BenchmarkController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
    Http3ServerApplication.class,
    BenchmarkController.class
})
public class Http3ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Http3ServerApplication.class, args);
    }
}
