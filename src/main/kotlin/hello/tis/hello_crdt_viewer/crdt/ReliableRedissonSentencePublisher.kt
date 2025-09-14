package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.redisson.api.RedissonClient
import org.redisson.client.RedisConnectionException
import org.redisson.client.RedisTimeoutException
import org.slf4j.LoggerFactory
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class ReliableRedissonSentencePublisher(
    private val redissonClient: RedissonClient,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Retryable(
        value = [RedisConnectionException::class, RedisTimeoutException::class],
        exclude = [JsonProcessingException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100, multiplier = 2.0, random = true)
    )
    fun publishTwoSentencesToDocument(documentId: String, twoSentenceResponse: TwoSentenceResponse) {
        val topic = "document.$documentId"
        val message = objectMapper.writeValueAsString(twoSentenceResponse)
        val patternTopic = redissonClient.getReliableTopic(topic)
        patternTopic.publish(message)
        logger.info("Published two sentences message to topic: $topic, message: $message")
    }

    @Recover
    fun recoverFromPublishFailure(ex: RuntimeException, documentId: String, twoSentenceResponse: TwoSentenceResponse) {
        val topic = "document.$documentId"
        val message = objectMapper.writeValueAsString(twoSentenceResponse)
        logger.error("모든 재시도 실패, 복구 로직 실행: channel={}, error={}, message={}", topic, ex.message, message)
        throw ex
    }
}
