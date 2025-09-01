package hello.tis.hello_crdt_viewer.crdt

import java.util.UUID


data class SentenceResponse(
    val id: String,
    val prevId: String,
    val rootDocumentId: String,
    val content: String,
    val sequence: UUID,
    val sessionId: String,
)
