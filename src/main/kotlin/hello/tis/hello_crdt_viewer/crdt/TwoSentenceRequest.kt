package hello.tis.hello_crdt_viewer.crdt

data class TwoSentenceRequest(
    val firstRequest: SentenceRequest,
    val secondRequest: SentenceRequest
)