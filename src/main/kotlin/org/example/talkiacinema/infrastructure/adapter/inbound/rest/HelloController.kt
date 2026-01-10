package org.example.talkiacinema.infrastructure.adapter.inbound.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/")
    fun home(): String {
        return "Hello World ! Mon serveur Spring Boot tourne sur Render 🎉"
    }
}