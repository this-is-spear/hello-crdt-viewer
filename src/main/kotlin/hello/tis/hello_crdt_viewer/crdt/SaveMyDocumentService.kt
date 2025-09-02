package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.databind.ObjectMapper
import hello.tis.hello_crdt_viewer.domain.MyDocument
import hello.tis.hello_crdt_viewer.domain.Sentence
import hello.tis.hello_crdt_viewer.repository.MyDocumentRepository
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.stream.StreamListener
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.Subscription
import org.springframework.stereotype.Service

const val MONGODB_SYNC_STREAM = "mongodb-sync"

@Service
class SaveMyDocumentService(
    private val streamMessageListenerContainer: StreamMessageListenerContainer<String, MapRecord<String, String, String>>,
    private val myDocumentRepository: MyDocumentRepository,
    private val objectMapper: ObjectMapper
) : StreamListener<String, MapRecord<String, String, String>> {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val consumerGroup = "document-saver"
    private val consumerName = "saver-instance"
    private lateinit var subscription: Subscription

    @PostConstruct
    fun initialize() {
        startListening()
    }

    @PreDestroy
    fun cleanup() {
        subscription.cancel()
        streamMessageListenerContainer.stop()
    }

    private fun startListening() {
        streamMessageListenerContainer.start()

        subscription = streamMessageListenerContainer.receiveAutoAck(
            Consumer.from(consumerGroup, consumerName),
            StreamOffset.create(MONGODB_SYNC_STREAM, ReadOffset.lastConsumed()),
            this
        )
    }

    override fun onMessage(message: MapRecord<String, String, String>) {
        try {
            val sentenceJson = message.value["sentence"] as String
            val sentence = objectMapper.readValue(sentenceJson, Sentence::class.java)
            log.debug("Processing sentence for MongoDB sync: {}", sentence)
            processDocument(sentence)
        } catch (e: Exception) {
            log.error("Error processing stream message: {}", message, e)
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
