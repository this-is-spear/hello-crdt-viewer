package hello.tis.hello_crdt_viewer.viewer

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewerController {

    @GetMapping("/")
    fun landing(): String {
        return "landing"
    }

    @GetMapping("/editor")
    fun editor(): String {
        return "editor"
    }
}