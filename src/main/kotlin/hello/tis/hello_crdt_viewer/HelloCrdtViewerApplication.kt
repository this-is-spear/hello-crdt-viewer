package hello.tis.hello_crdt_viewer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelloCrdtViewerApplication

fun main(args: Array<String>) {
    runApplication<HelloCrdtViewerApplication>(*args)
}
