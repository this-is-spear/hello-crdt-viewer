package hello.tis.hello_crdt_viewer.docs.service

import hello.tis.hello_crdt_viewer.domain.MyDocument
import hello.tis.hello_crdt_viewer.domain.MyDocumentMetadata
import hello.tis.hello_crdt_viewer.domain.SequenceCreator
import hello.tis.hello_crdt_viewer.repository.MyDocumentRepository
import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MyDocumentService(
    private val sequenceCreator: SequenceCreator,
    private val myDocumentRepository: MyDocumentRepository,
) {
    fun readDocument(id: String): MyDocument? {
        val document = myDocumentRepository.findByIdOrNull(id)
        return document?.let {
            MyDocument(
                id = it.id,
                title = it.title,
                sentences = it.getOrderedSentences(),
                lastUpdateSequence = it.lastUpdateSequence
            )
        }
    }

    fun getDocuments(): List<MyDocumentMetadata> {
        return myDocumentRepository.findAll().map { MyDocumentMetadata(it.id, it.title, it.lastUpdateSequence) }
    }

    fun createDocument(title: String): MyDocument {
        return myDocumentRepository.save(
            MyDocument(
                id = UUID.randomUUID().toString(),
                title = title,
                sentences = emptyList(),
                lastUpdateSequence = sequenceCreator.getNowTime(),
            )
        )
    }
}