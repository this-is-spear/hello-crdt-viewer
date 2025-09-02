package hello.tis.hello_crdt_viewer.docs

data class MyDocumentMetadata(
    val id: String,
    val title: String,
    val lastUpdateSequence: Long,
)
