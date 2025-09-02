package hello.tis.hello_crdt_viewer.docs

import hello.tis.hello_crdt_viewer.domain.MyDocument
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class DocumentController(
    private val myDocumentService: MyDocumentService,
) {
    @GetMapping("/documents/{id}")
    fun viewDocs(
        @PathVariable id: String,
    ): MyDocument {
        return myDocumentService.readDocument(id) ?: throw IllegalArgumentException("Document not found")
    }

    @PostMapping("/documents")
    fun createDocs(
        @RequestBody request: CreateDocumentRequest,
    ): MyDocument {
        return myDocumentService.createDocument(request.title)
    }

    @GetMapping("/documents")
    fun getDocs(): List<MyDocumentMetadata> {
        return myDocumentService.getDocuments()
    }
}
