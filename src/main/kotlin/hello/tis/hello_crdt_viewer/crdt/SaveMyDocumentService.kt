package hello.tis.hello_crdt_viewer.crdt

import hello.tis.hello_crdt_viewer.domain.MyDocument
import hello.tis.hello_crdt_viewer.domain.Sentence
import hello.tis.hello_crdt_viewer.repository.MyDocumentRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SaveMyDocumentService(
    private val sentenceRedisTemplate: RedisTemplate<String, SentenceResponse>,
    private val myDocumentRepository: MyDocumentRepository
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = 100) // 10초마다 실행
    fun processPendingDocuments() {
        try {
            // Redis List에서 모든 데이터를 가져온다
            val pendingSentences = mutableListOf<SentenceResponse>()
            
            // Redis List에서 데이터를 하나씩 pop하여 가져온다
            var sentenceResponse: SentenceResponse?
            while (true) {
                sentenceResponse = sentenceRedisTemplate.opsForList().rightPop(RedisMessageProducer.MONGODB_SYNC_LIST_KEY)
                if (sentenceResponse == null) break
                pendingSentences.add(sentenceResponse)
            }

            if (pendingSentences.isNotEmpty()) {
                log.info("Processing {} pending sentences from Redis list", pendingSentences.size)
                
                // SentenceResponse를 Sentence로 변환하여 처리
                pendingSentences.forEach { sentenceResponse ->
                    val sentence = Sentence(
                        id = sentenceResponse.id,
                        prevId = sentenceResponse.prevId,
                        rootDocumentId = sentenceResponse.rootDocumentId,
                        content = sentenceResponse.content,
                        sequence = sentenceResponse.sequence
                    )
                    processDocument(sentence)
                }
            }
        } catch (e: Exception) {
            log.error("Error processing pending documents from Redis list: {}", e.message, e)
        }
    }

    private fun processDocument(sentence: Sentence) {
        val existingDocument = myDocumentRepository.findById(sentence.rootDocumentId)

        // 동시성 제어 필요 문서 반영 작업은 하나씩만 진행한다.
        val document = if (existingDocument.isPresent) {
            // update
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
            // create
            MyDocument(
                id = sentence.rootDocumentId,
                title = sentence.rootDocumentId,
                sentences = listOf(sentence),
                lastUpdateSequence = sentence.sequence
            )
        }

        val savedDocument = myDocumentRepository.save(document)
        log.debug("Saved document {} with sentence {}", savedDocument.id, sentence.id)
    }
}
