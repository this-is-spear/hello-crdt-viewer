package hello.tis.hello_crdt_viewer.repository

import hello.tis.hello_crdt_viewer.domain.MyDocumentIndex
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MyDocumentIndexRepository: MongoRepository<MyDocumentIndex, String>
