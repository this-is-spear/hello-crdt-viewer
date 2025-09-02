package hello.tis.hello_crdt_viewer.crdt

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessagePublisher(
    private val sentenceRedisTemplate: RedisTemplate<String, SentenceResponse>

) {
    fun publishToDocument(documentId: String, sentenceResponse: SentenceResponse) {
        val topic = "document.$documentId"
        sentenceRedisTemplate.convertAndSend(topic, sentenceResponse)
    }
}
