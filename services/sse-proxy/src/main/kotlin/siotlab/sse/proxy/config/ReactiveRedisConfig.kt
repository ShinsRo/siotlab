package siotlab.sse.proxy.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
@EnableConfigurationProperties(RedisProperties::class)
class ReactiveRedisConfig(private val properties: RedisProperties) {
    @Bean
    fun reactiveRedisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(properties.host, properties.port)
    }

    @Bean
    fun stringRedisSerializer(): StringRedisSerializer {
        return StringRedisSerializer(Charsets.UTF_8)
    }

    @Bean
    fun genericJsonRedisSerializer(objectMapper: ObjectMapper): GenericJackson2JsonRedisSerializer {
        return GenericJackson2JsonRedisSerializer(objectMapper)
    }
}
