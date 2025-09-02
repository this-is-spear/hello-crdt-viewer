package hello.tis.hello_crdt_viewer.crdt

import java.time.Duration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.StreamReceiver

@Configuration
class RedisStreamConfiguration {
    @Bean
    fun streamMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
    ): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        val options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
            .builder()
            .pollTimeout(Duration.ofMillis(100))
            .build()

        return StreamMessageListenerContainer.create(connectionFactory, options)
    }

    @Bean
    fun streamReceiver(connectionFactory: ReactiveRedisConnectionFactory): StreamReceiver<String, MapRecord<String, String, String>> {
        val options = StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(100))
            .build()

        return StreamReceiver.create(connectionFactory, options)
    }
}