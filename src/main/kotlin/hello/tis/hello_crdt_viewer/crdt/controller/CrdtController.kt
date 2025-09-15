package hello.tis.hello_crdt_viewer.crdt.controller

import hello.tis.hello_crdt_viewer.crdt.service.MyDocumentIndexService
import hello.tis.hello_crdt_viewer.crdt.redis.RedisMessageProducer
import hello.tis.hello_crdt_viewer.crdt.redis.ReliableRedissonSentencePublisher
import hello.tis.hello_crdt_viewer.crdt.redis.ReliableRedissonSentenceSubscriber
import hello.tis.hello_crdt_viewer.domain.Sentence
import hello.tis.hello_crdt_viewer.domain.SequenceCreator
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
class CrdtController(
    private val sequenceCreator: SequenceCreator,
    private val myDocumentIndexService: MyDocumentIndexService,
    private val reliableRedissonSentencePublisher: ReliableRedissonSentencePublisher,
    private val redisMessageProducer: RedisMessageProducer,
    private val reliableRedissonSentenceSubscriber: ReliableRedissonSentenceSubscriber,
) {
    @MessageMapping("/sentence/publish/two")
    fun publishTwoSentences(@Payload twoSentenceRequest: TwoSentenceRequest) {
        val firstDocumentSequence = sequenceCreator.getNowTime()
        val firstProcessedSentence = myDocumentIndexService.updateKeyAndSyncMyDocumentIndex(
            Sentence(
                id = twoSentenceRequest.firstRequest.id,
                prevId = twoSentenceRequest.firstRequest.prevId,
                rootDocumentId = twoSentenceRequest.firstRequest.rootDocumentId,
                content = twoSentenceRequest.firstRequest.content,
                sequence = firstDocumentSequence,
                order = twoSentenceRequest.firstRequest.order,
            )
        )

        val firstProcessedSentenceResponse = SentenceResponse(
            id = firstProcessedSentence.id,
            prevId = firstProcessedSentence.prevId,
            rootDocumentId = firstProcessedSentence.rootDocumentId,
            content = firstProcessedSentence.content,
            sequence = firstProcessedSentence.sequence,
            order = firstProcessedSentence.order,
        )

        val secondDocumentSequence = sequenceCreator.getNowTime()
        val secondProcessedSentence = myDocumentIndexService.updateKeyAndSyncMyDocumentIndex (
            Sentence(
                id = twoSentenceRequest.secondRequest.id,
                prevId = twoSentenceRequest.secondRequest.prevId,
                rootDocumentId = twoSentenceRequest.secondRequest.rootDocumentId,
                content = twoSentenceRequest.secondRequest.content,
                sequence = secondDocumentSequence,
                order = twoSentenceRequest.secondRequest.order,
            )
        )

        val secondProcessedSentenceResponse = SentenceResponse(
            id = secondProcessedSentence.id,
            prevId = secondProcessedSentence.prevId,
            rootDocumentId = secondProcessedSentence.rootDocumentId,
            content = secondProcessedSentence.content,
            sequence = secondProcessedSentence.sequence,
            order = secondProcessedSentence.order,
        )

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
            twoSentenceRequest.secondRequest.rootDocumentId,
            twoSentenceResponse
        )
    }
}