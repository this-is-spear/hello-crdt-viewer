package hello.tis.hello_crdt_viewer.domain

import com.github.f4b6a3.ulid.UlidCreator
import java.util.UUID

/**
 * ulid 는 uuid 형식으로 관리하도록 구성했습니다.
 *
 *
 * - 화면에서 js 데이터 자료 구조 호환성을 고려해서 uuid 형식으로 반환합니다.
 * - DB에서 데이터 자료 구조 호환성을 고려해서 uuid 형식으로 변환합니다.
 */
class DocumentSequence(
    val sequence: UUID,
) {
    companion object {
        fun create(time: Long): DocumentSequence {
            val ulid = UlidCreator.getMonotonicUlid(time)
            return DocumentSequence(ulid.toUuid())
        }
    }
}