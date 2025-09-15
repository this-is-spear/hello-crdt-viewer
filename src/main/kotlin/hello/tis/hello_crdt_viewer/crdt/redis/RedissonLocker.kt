package hello.tis.hello_crdt_viewer.crdt.redis

import hello.tis.hello_crdt_viewer.crdt.service.Locker
import java.util.concurrent.TimeUnit
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RedissonLocker(
    private val redissonClient: RedissonClient,
) : Locker {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun <T> lock(lockName: String, function: () -> T): T {
        val threadId = Thread.currentThread().id
        val lock = redissonClient.getLock(lockName)
        try {
            lock.tryLock(10, 30, TimeUnit.SECONDS)
            require(lock.isLocked) { "Failed to get lock" }
            return function()
        } finally {
            if (lock.isLocked && lock.isHeldByThread(threadId)) {
                lock.unlock()
            } else {
                log.warn("$lockName 락 해제 실패, isLocked: ${lock.isLocked}, isHeldByThread: ${lock.isHeldByThread(threadId)}")
            }
        }
    }
}