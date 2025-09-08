package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.databind.ObjectMapper
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RedisMessageProducer(
    private val redissonClient: RedissonClient,
    private val objectMapper: ObjectMapper
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val DOCUMENT_LIST_KEY = "document:queue"
    }

    fun produceTwoSentencesToDocument(rootDocumentId: String, twoSentenceResponse: TwoSentenceResponse) {
        try {
            val queue = redissonClient.getDeque<String>(DOCUMENT_LIST_KEY)
            val serializedMessage = objectMapper.writeValueAsString(twoSentenceResponse)
            queue.addFirst(serializedMessage)
            log.debug("Added two sentences message to queue for document: {}", rootDocumentId)
        } catch (e: Exception) {
            log.error("Error producing two sentences message to queue for document {}: {}", rootDocumentId, e.message, e)
        }
    }
}
