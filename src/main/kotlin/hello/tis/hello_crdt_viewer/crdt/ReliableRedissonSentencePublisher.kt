package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.databind.ObjectMapper
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ReliableRedissonSentencePublisher(
    private val redissonClient: RedissonClient,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publishToDocument(documentId: String, sentenceResponse: SentenceResponse) {
        val topic = "document.$documentId"
        val message = objectMapper.writeValueAsString(sentenceResponse)
        
        // Pattern Topic으로 발행 (Subscriber와 호환)
        val patternTopic = redissonClient.getTopic(topic)
        patternTopic.publish(message)
        
        logger.info("Published message to topic: $topic, message: $message")
    }
}
