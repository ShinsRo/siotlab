package io.siolab.httpio.support

import org.mockserver.client.MockServerClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.mockserver.MockServerContainer
import org.testcontainers.utility.DockerImageName

private val MOCK_SERVER_IMAGE = DockerImageName
    .parse("mockserver/mockserver")
    .withTag("mockserver-${MockServerClient::class.java.`package`.implementationVersion}")

interface HttpIoContainers {
    val mockServer: MockServerContainer
        get() = Companion.mockServer

    val mockServerBaseUrl: String
        get() = mockServer.endpoint

    fun mockServerClient(): MockServerClient =
        MockServerClient(mockServer.host, mockServer.serverPort)

    companion object {
        @Container
        @JvmField
        val mockServer: MockServerContainer = MockServerContainer(MOCK_SERVER_IMAGE)
    }
}
