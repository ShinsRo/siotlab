package siotlab.sse.proxy.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.data.redis")
class RedisProperties(val host: String, val port: Int)
