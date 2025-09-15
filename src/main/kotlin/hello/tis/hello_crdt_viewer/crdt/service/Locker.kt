package hello.tis.hello_crdt_viewer.crdt.service

interface Locker {
    fun <T> lock(lockName: String, function: () -> T): T
}