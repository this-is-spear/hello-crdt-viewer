package hello.tis.hello_crdt_viewer.crdt.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import hello.tis.hello_crdt_viewer.crdt.redis.RedisMessageProducer
import hello.tis.hello_crdt_viewer.crdt.controller.SentenceResponse
import hello.tis.hello_crdt_viewer.crdt.controller.TwoSentenceResponse
import hello.tis.hello_crdt_viewer.domain.MyDocument
import hello.tis.hello_crdt_viewer.domain.Sentence
import hello.tis.hello_crdt_viewer.repository.MyDocumentRepository
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SaveMyDocumentService(
    private val redissonClient: RedissonClient,
    private val locker: Locker,
    private val objectMapper: ObjectMapper,
    private val myDocumentRepository: MyDocumentRepository,
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = 100)
    fun processPendingDocuments() {
        try {
            val queue = redissonClient.getDeque<String>(RedisMessageProducer.Companion.DOCUMENT_LIST_KEY)
            val pendingSentences = mutableListOf<SentenceResponse>()
            val messages = queue.pollLast(1000)
            messages.forEach { message ->
                // Try to parse as TwoSentenceResponse first
                val twoSentenceResponse = objectMapper.readValue<TwoSentenceResponse>(message)
                pendingSentences.add(twoSentenceResponse.firstResponse)
                pendingSentences.add(twoSentenceResponse.secondResponse)
            }

            if (pendingSentences.isNotEmpty()) {
                pendingSentences.forEach { sentenceResponse ->
                    val sentence = Sentence(
                        id = sentenceResponse.id,
                        prevId = sentenceResponse.prevId,
                        rootDocumentId = sentenceResponse.rootDocumentId,
                        content = sentenceResponse.content,
                        sequence = sentenceResponse.sequence,
                        order = sentenceResponse.order
                    )
                    processDocument(sentence)
                }
            }
        } catch (e: Exception) {
            log.error("Error processing pending documents from Redis queue: {}", e.message, e)
        }
    }

    private fun processDocument(sentence: Sentence) {
        val existingDocument = myDocumentRepository.findById(sentence.rootDocumentId)
        val writeDocumentLockName = "document:${sentence.rootDocumentId}:write"
        // 동시성 제어 필요 문서 반영 작업은 하나씩만 진행한다.
        val document = locker.lock(writeDocumentLockName) {
            if (existingDocument.isPresent) {
                val myDocument = existingDocument.get()
                // 동일한 문장인 경우 sequence 순서에 따라 덮어쓴다.
                val allSentences = (myDocument.sentences + listOf(sentence))
                    .groupBy { it.id }
                    .values
                    .map { sentencesWithSameId -> sentencesWithSameId.maxBy { it.sequence } }

                MyDocument(
                    id = myDocument.id,
                    title = myDocument.title,
                    sentences = allSentences,
                    lastUpdateSequence = sentence.sequence
                )
            } else {
                MyDocument(
                    id = sentence.rootDocumentId,
                    title = sentence.rootDocumentId,
                    sentences = listOf(sentence),
                    lastUpdateSequence = sentence.sequence
                )
            }
        }

        val savedDocument = myDocumentRepository.save(document)
        log.debug("Saved document {} with sentence {}", savedDocument.id, sentence.id)
    }
}