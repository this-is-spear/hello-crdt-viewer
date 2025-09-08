package hello.tis.hello_crdt_viewer

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class HelloCrdtViewerApplicationTests {

    @Test
    fun contextLoads() {
    }
}
