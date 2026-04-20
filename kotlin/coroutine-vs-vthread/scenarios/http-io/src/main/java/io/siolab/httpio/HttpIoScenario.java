package io.siolab.httpio;

public interface HttpIoScenario {
    HttpIoResult run(HttpIoRequest request, String baseUrl) throws Exception;
}
