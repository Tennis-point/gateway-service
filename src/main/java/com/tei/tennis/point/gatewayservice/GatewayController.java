package com.tei.tennis.point.gatewayservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tei.tennis.point.gatewayservice.model.Game;
import com.tei.tennis.point.gatewayservice.model.User;
import com.tei.tennis.point.gatewayservice.presentation.GameResult;
import com.tei.tennis.point.gatewayservice.presentation.ToResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@CrossOrigin("http://localhost:4200/")
public class GatewayController {

    public static final String AUTH_SERVICE = "http://localhost:8081/api/v1/user/";
    public static final String TENIS_SERVICE = "http://localhost:4000/";
    public static final String REPORTING_SERVICE = "http://localhost:3000/";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws JsonProcessingException {

        HttpRequest request = HttpRequest.newBuilder()
            .header("Content-type", "application/json")
            .uri(URI.create(AUTH_SERVICE + "login"))
            .POST(HttpRequest.BodyPublishers.ofString(
                new ObjectMapper().writeValueAsString(user)
            ))
            .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(response.body());
                Map value = objectMapper.readValue(response.body(), Map.class);
                if (!value.containsKey("token")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(value);
                } else {
                    ResponseEntity.of(Optional.of(value));
                }
                return ResponseEntity.of(
                    Optional.of(value)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<Map> register(@RequestBody User user) throws JsonProcessingException {
        HttpRequest request = HttpRequest.newBuilder()
            .header("Content-type", "application/json")
            .uri(URI.create(AUTH_SERVICE + "register"))
            .POST(HttpRequest.BodyPublishers.ofString(
                new ObjectMapper().writeValueAsString(user)
            ))
            .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info(response.body());
                return ResponseEntity.of(
                    Optional.of(objectMapper.readValue(response.body(), Map.class))
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @PostMapping("/game/{gameId}/point/{side}")
    public Game addPoint(@PathVariable String gameId, @PathVariable String side, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TENIS_SERVICE + "game/" + gameId + "/point/" + side))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Authorization", token).build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(response.body(), Game.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @PostMapping("/game/{userId}/start")
    public Game startGame(@PathVariable String userId, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TENIS_SERVICE + "game/" + userId + "/start"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Authorization", token).build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(response.body(), Game.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @GetMapping("/game/{userId}/")
    public List<GameResult> getByUser(@PathVariable String userId, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(TENIS_SERVICE + "game/" + userId + "/"))
            .GET()
            .header("Authorization", token)
            .header("Content-type", "application/json")
            .header("Access-Control-Allow-Origin", "http://localhost:8080")
            .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return Arrays.stream(objectMapper.readValue(response.body(), Game[].class)).map(ToResult::execute).toList();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @GetMapping(value = "/report/{userId}/game/{gameId}/")
    public Map<String, String> getReport(@PathVariable String userId, @PathVariable String gameId, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(REPORTING_SERVICE + "report/" + userId + "/game/"+gameId+"/"))
            .GET()
            .header("Authorization", token)
            .header("Content-type", "application/json")
            .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response != null) {
            return Map.of("link", response.body());
        }
        return null;
    }
}
