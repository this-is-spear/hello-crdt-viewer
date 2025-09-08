package hello.tis.hello_crdt_viewer.domain

data class Sentence(
    val id: String,
    val prevId: String,
    val rootDocumentId: String,
    val content: String,
    val sequence: Long,
    val order: String,
)
