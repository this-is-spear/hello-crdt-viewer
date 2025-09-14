package hello.tis.hello_crdt_viewer.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.support.RetryTemplate

@EnableRetry
@Configuration
class RetryConfig {
    @Bean
    fun retryTemplate(): RetryTemplate = RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000L, 2.0, 10000L)
        .build()
}
