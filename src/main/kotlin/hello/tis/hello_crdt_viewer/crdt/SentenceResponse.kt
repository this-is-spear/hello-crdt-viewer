package hello.tis.hello_crdt_viewer.crdt

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class SentenceResponse @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("prevId") val prevId: String,
    @JsonProperty("rootDocumentId") val rootDocumentId: String,
    @JsonProperty("content") val content: String,
    @JsonProperty("sequence") val sequence: Long,
    @JsonProperty("sessionId") val sessionId: String,
    @JsonProperty("order") val order: String,
)
