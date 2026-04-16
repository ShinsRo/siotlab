package io.siolab.httpio.support

import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

private const val NGINX_HTTP_PORT = 80
private val NGINX_IMAGE = DockerImageName.parse("nginx:1.23.2")

interface HttpIoContainers {
    val nginx: GenericContainer<*>
        get() = Companion.nginx

    @Suppress("HttpUrlsUsage")
    val nginxBaseUrl: String
        get() {
            val mappedHttpPort = nginx.getMappedPort(NGINX_HTTP_PORT)
            return "http://${nginx.host}:$mappedHttpPort"
        }

    companion object {
        @Container
        @JvmField
        val nginx: GenericContainer<*> = GenericContainer(NGINX_IMAGE)
            .withExposedPorts(NGINX_HTTP_PORT)
    }
}
