package hello.tis.hello_crdt_viewer.crdt

import com.davidarvelo.fractionalindexing.FractionalIndexing
import hello.tis.hello_crdt_viewer.domain.MyDocumentIndex
import hello.tis.hello_crdt_viewer.domain.Sentence
import hello.tis.hello_crdt_viewer.repository.MyDocumentIndexRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MyDocumentIndexService(
    private val myDocumentIndexRepository: MyDocumentIndexRepository,
    private val locker: Locker,
) {
    fun updateKeyAndSyncMyDocumentIndex(sentence: Sentence): Sentence {
        return locker.lock("index:${sentence.rootDocumentId}:write") {

            val myDocumentIndex = myDocumentIndexRepository.findByIdOrNull(sentence.rootDocumentId)
                ?: myDocumentIndexRepository.save(MyDocumentIndex(sentence.rootDocumentId))

            if (!myDocumentIndex.fractionIndex.contains(sentence.order)) {
                val newMyDocumentIndex = MyDocumentIndex(
                    id = myDocumentIndex.id,
                    fractionIndex = (myDocumentIndex.fractionIndex + sentence.order).sorted()
                )
                myDocumentIndexRepository.save(newMyDocumentIndex)
                sentence
            } else {
                val indexOf = myDocumentIndex.fractionIndex.indexOf(sentence.order)
                val fractionalIndex = if (indexOf == 0) {
                    // 첫 위치인 경우
                    FractionalIndexing.generateFractionalIndexBetween(null, sentence.order)
                } else {
                    // 계산 가능한 경우
                    FractionalIndexing.generateFractionalIndexBetween(
                        myDocumentIndex.fractionIndex[indexOf - 1],
                        sentence.order
                    )
                }
                val newMyDocumentIndex = MyDocumentIndex(
                    id = myDocumentIndex.id,
                    fractionIndex = (myDocumentIndex.fractionIndex + fractionalIndex).sorted()
                )
                myDocumentIndexRepository.save(newMyDocumentIndex)

                Sentence(
                    id = sentence.id,
                    prevId = sentence.prevId,
                    rootDocumentId = sentence.rootDocumentId,
                    content = sentence.content,
                    sequence = sentence.sequence,
                    order = fractionalIndex,
                )
            }
        }
    }
}