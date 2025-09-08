package hello.tis.hello_crdt_viewer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class MyDocument(
    @Id
    val id: String,
    val title: String,
    val sentences: List<Sentence>,
    val lastUpdateSequence: Long,
) {
    fun getOrderedSentences(): List<Sentence> {
        if (sentences.isEmpty()) return emptyList()
        val rootSentences = sentences
            .sortedBy { it.order }
        return rootSentences
    }
}
