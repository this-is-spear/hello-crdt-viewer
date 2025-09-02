package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.databind.ObjectMapper
import hello.tis.hello_crdt_viewer.domain.Sentence
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessageProducer(
    private val sentenceRedisTemplate: RedisTemplate<String, SentenceResponse>
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val MONGODB_SYNC_LIST_KEY = "mongodb:sync:list"
        const val MONGODB_SYNC_CHANNEL = "mongodb:sync:channel"
    }

    fun produceToDocument(rootDocumentId: String, processedSentenceResponse: SentenceResponse) {
        try {
            val sentence = Sentence(
                id = processedSentenceResponse.id,
                prevId = processedSentenceResponse.prevId,
                rootDocumentId = processedSentenceResponse.rootDocumentId,
                content = processedSentenceResponse.content,
                sequence = processedSentenceResponse.sequence
            )

            // 1. Redis List에 데이터를 추가합니다.
            sentenceRedisTemplate.opsForList().leftPush(MONGODB_SYNC_LIST_KEY, processedSentenceResponse)
            log.info("Produced message to list {} for sentence: {}", MONGODB_SYNC_LIST_KEY, sentence.id)
        } catch (e: Exception) {
            log.error("Error producing message to list for document {}: {}", rootDocumentId, e.message, e)
        }
    }
}
