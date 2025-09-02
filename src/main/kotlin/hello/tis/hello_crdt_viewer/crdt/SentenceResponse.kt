package hello.tis.hello_crdt_viewer.crdt


data class SentenceResponse(
    val id: String,
    val prevId: String,
    val rootDocumentId: String,
    val content: String,
    val sequence: Long,
    val sessionId: String,
)
