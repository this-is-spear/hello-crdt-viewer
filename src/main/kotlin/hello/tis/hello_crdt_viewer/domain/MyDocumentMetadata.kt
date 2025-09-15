package hello.tis.hello_crdt_viewer.domain

data class MyDocumentMetadata(
    val id: String,
    val title: String,
    val lastUpdateSequence: Long,
)