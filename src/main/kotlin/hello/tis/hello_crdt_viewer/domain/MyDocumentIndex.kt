package hello.tis.hello_crdt_viewer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class MyDocumentIndex(
    @Id
    val id: String,
    val fractionIndex: List<String> = emptyList(),
)
