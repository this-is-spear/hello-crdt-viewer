package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class DocumentMessageSubscriber(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val objectMapper: ObjectMapper
) {

    fun onMessage(message: String, channel: String) {
        try {
            val sentenceResponse = objectMapper.readValue(message, SentenceResponse::class.java)
            val documentId = sentenceResponse.rootDocumentId
            val topic = "/topic/document/$documentId"
            simpMessagingTemplate.convertAndSend(topic, sentenceResponse)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

