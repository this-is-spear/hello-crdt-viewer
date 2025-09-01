package hello.tis.hello_crdt_viewer.crdt

import hello.tis.hello_crdt_viewer.domain.DocumentSequence
import hello.tis.hello_crdt_viewer.domain.Sentence
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class CrdtController(
    private val sequenceCreatorService: SequenceCreatorService,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {
    @MessageMapping("/sentence/publish")
    fun publishSentence(@Payload sentenceRequest: SentenceRequest) {
        val now = sequenceCreatorService.getNowTime()
        val documentSequence = DocumentSequence.create(now)
        val processedSentence = Sentence(
            id = sentenceRequest.id,
            prevId = sentenceRequest.prevId,
            rootDocumentId = sentenceRequest.rootDocumentId,
            content = sentenceRequest.content,
            sequence = documentSequence.sequence,
        )

        val processedSentenceResponse = SentenceResponse(
            id = sentenceRequest.id,
            prevId = sentenceRequest.prevId,
            rootDocumentId = sentenceRequest.rootDocumentId,
            content = sentenceRequest.content,
            sequence = processedSentence.sequence,
            sessionId = sentenceRequest.sessionId,
        )
        
        // Publish to all subscribers of this document
        val topic = "/topic/document/${sentenceRequest.rootDocumentId}"
        simpMessagingTemplate.convertAndSend(topic, processedSentenceResponse)
    }
}
