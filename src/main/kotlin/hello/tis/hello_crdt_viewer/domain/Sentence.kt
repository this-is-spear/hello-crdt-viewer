package hello.tis.hello_crdt_viewer.domain

import java.util.UUID

data class Sentence(
    val id: String,
    val prevId: String,
    val rootDocumentId: String,
    val content: String,
    val sequence: UUID,
)
