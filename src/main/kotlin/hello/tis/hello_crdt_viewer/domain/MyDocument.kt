package hello.tis.hello_crdt_viewer.domain

class MyDocument(
    val id: String,
    val title: String,
    val sentences: List<Sentence>,
    val lastUpdateSequence: Long,
)
