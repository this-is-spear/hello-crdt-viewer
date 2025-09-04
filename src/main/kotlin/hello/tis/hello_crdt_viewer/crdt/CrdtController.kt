package hello.tis.hello_crdt_viewer.crdt

import hello.tis.hello_crdt_viewer.domain.Sentence
import hello.tis.hello_crdt_viewer.domain.SequenceCreator
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
class CrdtController(
    private val sequenceCreator: SequenceCreator,
    private val reliableRedissonSentencePublisher: ReliableRedissonSentencePublisher,
    private val redisMessageProducer: RedisMessageProducer,
    private val reliableRedissonSentenceSubscriber: ReliableRedissonSentenceSubscriber,
) {
    @MessageMapping("/sentence/publish")
    fun publishSentence(@Payload sentenceRequest: SentenceRequest) {
        val documentSequence = sequenceCreator.getNowTime()
        val processedSentence = Sentence(
            id = sentenceRequest.id,
            prevId = sentenceRequest.prevId,
            rootDocumentId = sentenceRequest.rootDocumentId,
            content = sentenceRequest.content,
            sequence = documentSequence,
        )

        val processedSentenceResponse = SentenceResponse(
            id = sentenceRequest.id,
            prevId = sentenceRequest.prevId,
            rootDocumentId = sentenceRequest.rootDocumentId,
            content = sentenceRequest.content,
            sequence = processedSentence.sequence,
            sessionId = sentenceRequest.sessionId,
        )

        reliableRedissonSentenceSubscriber.subscribeToDocument(sentenceRequest.rootDocumentId)
        reliableRedissonSentencePublisher.publishToDocument(
            sentenceRequest.rootDocumentId,
            processedSentenceResponse
        )
        redisMessageProducer.produceToDocument(
            sentenceRequest.rootDocumentId,
            processedSentenceResponse
        )
    }
}
