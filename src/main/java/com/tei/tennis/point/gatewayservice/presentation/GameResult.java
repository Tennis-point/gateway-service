package com.tei.tennis.point.gatewayservice.presentation;

import lombok.Data;

import java.util.List;

@Data
public class GameResult {

    private List<SetResult> sets;
    private String p1Result;
    private String p2Result;
    private String gameId;

    public String getP1Result() {
        return String.valueOf((int) sets.stream().filter(s -> Integer.parseInt(s.getP1Result()) > Integer.parseInt(s.getP2Result())).count());
    }

    public String getP2Result() {
        return String.valueOf((int) sets.stream().filter(s -> Integer.parseInt(s.getP2Result()) > Integer.parseInt(s.getP1Result())).count());
    }
}
