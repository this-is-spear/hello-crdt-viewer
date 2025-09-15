package hello.tis.hello_crdt_viewer.crdt.controller

data class SentenceRequest(
    val id: String,
    val prevId: String,
    val rootDocumentId: String,
    val content: String,
    val order: String,
)
