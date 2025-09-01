package hello.tis.hello_crdt_viewer.domain

import java.util.UUID

class MyDocument(
    val id: String,
    val title: String,
    val sentences: List<Sentence>,
    val lastUpdateSequence: UUID,
)
