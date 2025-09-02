package hello.tis.hello_crdt_viewer.repository

import hello.tis.hello_crdt_viewer.domain.MyDocument
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MyDocumentRepository : MongoRepository<MyDocument, String>
