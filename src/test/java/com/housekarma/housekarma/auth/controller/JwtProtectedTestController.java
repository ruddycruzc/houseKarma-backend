
package com.housekarma.housekarma.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtProtectedTestController {

    @GetMapping("/api/test/protected")
    public String recursoProtegido() {
        return "Acceso autorizado";
    }
}
