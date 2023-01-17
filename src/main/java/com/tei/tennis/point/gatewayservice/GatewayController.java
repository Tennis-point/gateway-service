package com.tei.tennis.point.gatewayservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tei.tennis.point.gatewayservice.model.Game;
import com.tei.tennis.point.gatewayservice.model.User;
import com.tei.tennis.point.gatewayservice.presentation.GameResult;
import com.tei.tennis.point.gatewayservice.presentation.ToResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${auth}")
    public String AUTH_SERVICE;
    @Value("${tennis}")
    public String TENNIS_SERVICE;
    @Value("${reporting}")
    public String REPORTING_SERVICE;

    @PostMapping("/login")
    @Operation(description = "Login endpoint for existing users only.")
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
            log.info("GATEWAY POST login: " + request.uri());
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
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(value);
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
    @Operation(description = "Registration endpoint new users.")
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
            log.info("GATEWAY POST register: " + request.uri());
            response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (response != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
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
    @Operation(description = "Calling this endpoint will add the point to the specified side of the game, unless the game is not already over.")
    public Game addPoint(@PathVariable String gameId, @PathVariable String side, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TENNIS_SERVICE + "game/" + gameId + "/point/" + side))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Authorization", token).build();

        HttpResponse<String> response = null;
        try {
            log.info("GATEWAY POST addPoint: " + request.uri());
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
    @Operation(description = "Start the game for the specified user.")
    public Game startGame(@PathVariable String userId, HttpServletRequest servletRequest) {
        String token = servletRequest.getHeader("Authorization");
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(TENNIS_SERVICE + "game/" + userId + "/start"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Authorization", token).build();

        HttpResponse<String> response = null;
        try {
            log.info("GATEWAY POST start game: " + request.uri());
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
    @Operation(description = "Retrieve all games for the user with a specific 'userId'.")
    public List<GameResult> getByUser(@PathVariable String userId, HttpServletRequest servletRequest) {
        return new Command().getByUser(servletRequest, (TENNIS_SERVICE + "game/" + userId + "/"));
    }

    @GetMapping(value = "/report/{userId}/game/{gameId}/")
    @Operation(description = "Returns the google sheet link with the report of the specified game.")
    public Map<String, String> getReport(@PathVariable String userId, @PathVariable String gameId, HttpServletRequest servletRequest) {
        return new Command().getReport(servletRequest, REPORTING_SERVICE + "report/" + userId + "/game/" + gameId + "/");
    }

    @GetMapping(value = "/url")
    @Operation(description = "Returns the google sheet link with the report of the specified game.")
    public Map<String, String> getUrl(HttpServletRequest servletRequest) {
        return new Command().getUrl(servletRequest, REPORTING_SERVICE + "url");
    }
}
