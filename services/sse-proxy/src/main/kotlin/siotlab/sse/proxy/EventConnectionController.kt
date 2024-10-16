package siotlab.sse.proxy

import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class EventConnectionController(
    private val sseEventConnector: SseEventConnector
) {
    @GetMapping("/api/event/v1/connection")
    fun getConnection(
        @RequestParam id: String,
    ): Flux<ServerSentEvent<String>> {
        return sseEventConnector.connect(id)
    }
}
