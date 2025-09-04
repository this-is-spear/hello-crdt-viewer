package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ReliableRedissonSentenceSubscriber(
    private val redissonClient: RedissonClient,
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val subscribedDocuments = ConcurrentHashMap<String, Boolean>()

    @PostConstruct
    fun subscribeReliable() {
        // 연결 테스트
        testRedisConnection()
    }

    fun subscribeToDocument(documentId: String) {
        if (subscribedDocuments.containsKey(documentId)) {
            logger.debug("Document $documentId is already subscribed")
            return
        }

        try {
            val topic = redissonClient.getTopic("document.$documentId")
            topic.addListener(String::class.java) { channel, sentence ->
                try {
                    logger.info("Received message for document: $documentId")
                    val sentenceResponse = objectMapper.readValue(sentence, SentenceResponse::class.java)
                    val webSocketTopic = "/topic/document/$documentId"
                    simpMessagingTemplate.convertAndSend(webSocketTopic, sentenceResponse)
                    logger.info("Forwarded message to WebSocket topic: $webSocketTopic")
                } catch (e: Exception) {
                    logger.error("Failed to process message for document: $documentId", e)
                }
            }
            subscribedDocuments[documentId] = true
            logger.info("Successfully subscribed to document: $documentId")
        } catch (e: Exception) {
            logger.error("Failed to subscribe to document: $documentId", e)
        }
    }

    private fun testRedisConnection() {
        try {
            val testTopic = redissonClient.getTopic("test-connection")
            testTopic.addListener(String::class.java) { channel, message ->
                logger.info("Redis connection test successful: $message")
            }
            testTopic.publish("Hello from subscriber")
            logger.info("Redis connection established successfully")
        } catch (e: Exception) {
            logger.error("Failed to establish Redis connection", e)
        }
    }
}
