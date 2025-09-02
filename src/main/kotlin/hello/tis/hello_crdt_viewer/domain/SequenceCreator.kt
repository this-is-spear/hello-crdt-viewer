package hello.tis.hello_crdt_viewer.domain

import org.springframework.stereotype.Service

/**
 * 시퀀스 생성시 애플리케이션 노드 간 시간 동기화가 필수 입니다.
 * 두 개 이상 노드 생성시 NTP 동기화 대비해서 타임스탬프 생성 기능을 분리합니다.
 */
@Service
class SequenceCreator {
    fun getNowTime(): Long {
        return System.currentTimeMillis()
    }
}
