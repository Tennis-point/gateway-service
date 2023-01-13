package com.tei.tennis.point.gatewayservice;

import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class GatewayController {

    public static final String AUTH_SERVICE = "http://localhost:8081/";

    @SneakyThrows
    @GetMapping("/login")
    public void login(HttpServletResponse response) {
        response.sendRedirect(AUTH_SERVICE);
    }

    @GetMapping("/success")
    public void successLogin() {

    }

}
