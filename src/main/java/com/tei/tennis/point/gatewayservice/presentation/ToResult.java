package com.tei.tennis.point.gatewayservice.presentation;

import com.tei.tennis.point.gatewayservice.model.Game;

import java.util.List;

public class ToResult {

    public static GameResult execute(Game game) {
        List<SetResult> setResults = game.getSets().stream().map(s -> {
            long p1 = s.getGems().stream().filter(gem -> gem.getWinner() != null).filter(gem -> gem.getWinner().equals("p1")).count();
            long p2 = s.getGems().stream().filter(gem -> gem.getWinner() != null).filter(gem -> gem.getWinner().equals("p2")).count();

            SetResult setResult = new SetResult();
            setResult.setP1Result(String.valueOf(p1));
            setResult.setP2Result(String.valueOf(p2));

            return setResult;
        }).toList();

        GameResult gameResult = new GameResult();
        gameResult.setGameId(game.getGameId());
        gameResult.setSets(setResults);
        gameResult.setP1Result(getResultOf("p1", gameResult));
        gameResult.setP1Result(getResultOf("p2", gameResult));

        return gameResult;
    }

    private static String getResultOf(String player, GameResult gameResult) {
        if (player.equals("p1")) {
            return String.valueOf(
                (int) gameResult.getSets().stream().filter(s -> Integer.parseInt(s.getP1Result()) > Integer.parseInt(s.getP2Result())).count());
        } else {
            return String.valueOf(
                (int) gameResult.getSets().stream().filter(s -> Integer.parseInt(s.getP1Result()) < Integer.parseInt(s.getP2Result())).count());
        }
    }
}
