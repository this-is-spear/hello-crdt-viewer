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

    fun publishTwoSentencesToDocument(documentId: String, twoSentenceResponse: TwoSentenceResponse) {
        val topic = "document.$documentId"
        val message = objectMapper.writeValueAsString(twoSentenceResponse)
        val patternTopic = redissonClient.getTopic(topic)
        patternTopic.publish(message)
        logger.info("Published two sentences message to topic: $topic, message: $message")
    }
}
