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
    @MessageMapping("/sentence/publish/two")
    fun publishTwoSentences(@Payload twoSentenceRequest: TwoSentenceRequest) {
        val firstDocumentSequence = sequenceCreator.getNowTime()
        val firstProcessedSentence = Sentence(
            id = twoSentenceRequest.firstRequest.id,
            prevId = twoSentenceRequest.firstRequest.prevId,
            rootDocumentId = twoSentenceRequest.firstRequest.rootDocumentId,
            content = twoSentenceRequest.firstRequest.content,
            sequence = firstDocumentSequence,
            order = twoSentenceRequest.firstRequest.order,
        )

        val firstProcessedSentenceResponse = SentenceResponse(
            id = twoSentenceRequest.firstRequest.id,
            prevId = twoSentenceRequest.firstRequest.prevId,
            rootDocumentId = twoSentenceRequest.firstRequest.rootDocumentId,
            content = twoSentenceRequest.firstRequest.content,
            sequence = firstProcessedSentence.sequence,
            sessionId = twoSentenceRequest.firstRequest.sessionId,
            order = twoSentenceRequest.firstRequest.order,
        )

        // Process second sentence
        val secondDocumentSequence = sequenceCreator.getNowTime()
        val secondProcessedSentence = Sentence(
            id = twoSentenceRequest.secondRequest.id,
            prevId = twoSentenceRequest.secondRequest.prevId,
            rootDocumentId = twoSentenceRequest.secondRequest.rootDocumentId,
            content = twoSentenceRequest.secondRequest.content,
            sequence = secondDocumentSequence,
            order = twoSentenceRequest.secondRequest.order,
        )

        val secondProcessedSentenceResponse = SentenceResponse(
            id = twoSentenceRequest.secondRequest.id,
            prevId = twoSentenceRequest.secondRequest.prevId,
            rootDocumentId = twoSentenceRequest.secondRequest.rootDocumentId,
            content = twoSentenceRequest.secondRequest.content,
            sequence = secondProcessedSentence.sequence,
            sessionId = twoSentenceRequest.secondRequest.sessionId,
            order = twoSentenceRequest.secondRequest.order,
        )

        // Create TwoSentenceResponse and send as a single unit
        val twoSentenceResponse = TwoSentenceResponse(
            firstResponse = firstProcessedSentenceResponse,
            secondResponse = secondProcessedSentenceResponse
        )

        reliableRedissonSentenceSubscriber.subscribeToDocument(twoSentenceRequest.firstRequest.rootDocumentId)
        reliableRedissonSentencePublisher.publishTwoSentencesToDocument(
            twoSentenceRequest.firstRequest.rootDocumentId,
            twoSentenceResponse
        )
        redisMessageProducer.produceTwoSentencesToDocument(
            twoSentenceRequest.firstRequest.rootDocumentId,
            twoSentenceResponse
        )
    }
}
