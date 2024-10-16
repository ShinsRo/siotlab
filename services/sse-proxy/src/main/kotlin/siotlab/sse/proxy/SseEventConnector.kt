package siotlab.sse.proxy

import io.github.oshai.kotlinlogging.KotlinLogging
import io.lettuce.core.RedisClient
import io.lettuce.core.pubsub.RedisPubSubAdapter
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

private val log = KotlinLogging.logger { }

@Component
class SseEventConnector {
    private val redisClient = RedisClient.create("redis://localhost:6379")
    private var redisConnection: StatefulRedisPubSubConnection<String, String>? = null

    fun connect(id: String): Flux<ServerSentEvent<String>> {
        if (redisConnection?.isOpen != true) {
            redisConnection = redisClient.connectPubSub()
        }

        return createInbound(channel = id, connection = redisConnection!!).map {
            ServerSentEvent.builder<String>()
                .id(id)
                .data(it)
                .build()
        }
    }

    private fun createInbound(
        channel: String,
        connection: StatefulRedisPubSubConnection<String, String>
    ): Flux<String> {
        return Flux.create { sink ->
            val listener = object : RedisPubSubAdapter<String, String>() {
                override fun message(channel: String, message: String?) {
                    log.info { "channel: $channel, message: $message" }
                    sink.next(message ?: "")
                }
            }

            connection.addListener(listener)
            val selection = connection.async()
            selection.subscribe(channel)

            sink.onDispose {
                selection.unsubscribe(channel)
                connection.removeListener(listener)
            }
        }
    }
}