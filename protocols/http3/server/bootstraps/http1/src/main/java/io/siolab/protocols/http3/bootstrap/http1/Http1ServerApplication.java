package io.siolab.protocols.http3.bootstrap.http1;

import io.siolab.protocols.http3.endpoints.BenchmarkController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
    Http1ServerApplication.class,
    BenchmarkController.class
})
public class Http1ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Http1ServerApplication.class, args);
    }
}
