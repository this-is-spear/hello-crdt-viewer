package hello.tis.hello_crdt_viewer.crdt.controller

data class TwoSentenceResponse(
    val firstResponse: SentenceResponse,
    val secondResponse: SentenceResponse
)