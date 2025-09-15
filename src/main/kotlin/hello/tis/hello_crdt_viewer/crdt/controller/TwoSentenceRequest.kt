package hello.tis.hello_crdt_viewer.crdt.controller

data class TwoSentenceRequest(
    val firstRequest: SentenceRequest,
    val secondRequest: SentenceRequest
)