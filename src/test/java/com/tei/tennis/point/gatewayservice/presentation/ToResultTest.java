package com.tei.tennis.point.gatewayservice.presentation;

import com.tei.tennis.point.gatewayservice.model.Game;
import com.tei.tennis.point.gatewayservice.model.Gem;
import com.tei.tennis.point.gatewayservice.model.Set;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToResultTest {

    @Test
    void shouldTransformToResult_2_0_6_0_6_0() {
        // given
        Set s1 = new Set("set1", "p1", List.of(p1Gem(), p1Gem(), p1Gem(), p1Gem(), p1Gem(), p1Gem())); // 6:0
        Set s2 = new Set("set2", "p1", List.of(p1Gem(), p1Gem(), p1Gem(), p1Gem(), p1Gem(), p1Gem())); // 6:0
        Game game = new Game("gameId", "userId", "p1", List.of(s1, s2));

        // when
        GameResult result = ToResult.execute(game);

        // then // 2:0 6:0 6:0
        assertEquals(result.getP1Result(), "2");
        assertEquals(result.getP2Result(), "0");
        assertEquals(result.getSets().get(0).getP1Result(), "6");
        assertEquals(result.getSets().get(0).getP2Result(), "0");
    }

    @Test
    void shouldTransformToResult_1_2_6_0_3_6_5_7() {
        // given
        Set s1 = new Set("set1", "p1", List.of(p1Gem(), p1Gem(), p1Gem(), p1Gem(), p1Gem(), p1Gem())); // 6:0
        Set s2 = new Set("set2", "p1", List.of(p2Gem(), p2Gem(), p2Gem(), p1Gem(), p1Gem(), p1Gem(), p2Gem(), p2Gem(), p2Gem())); // 3:6
        Set s3 = new Set("set3", "p2",
            List.of(p2Gem(), p2Gem(), p2Gem(), // 0:3
                p1Gem(), p1Gem(), p1Gem(), // 3:3
                p2Gem(), p2Gem(), // 3:5
                p1Gem(), p1Gem(),  // 5:5
                p2Gem(), p2Gem())); // 5:7
        Game game = new Game("gameId", "userId", "p1", List.of(s1, s2, s3));

        // when
        GameResult result = ToResult.execute(game);

        // then // 2:0 6:0 6:0
        assertEquals(result.getP1Result(), "1");
        assertEquals(result.getP2Result(), "2");

        assertEquals(result.getSets().get(0).getP1Result(), "6");
        assertEquals(result.getSets().get(0).getP2Result(), "0");

        assertEquals(result.getSets().get(1).getP1Result(), "3");
        assertEquals(result.getSets().get(1).getP2Result(), "6");

        assertEquals(result.getSets().get(2).getP1Result(), "5");
        assertEquals(result.getSets().get(2).getP2Result(), "7");
    }

    private static Gem p1Gem() {
        return new Gem("g1", "FORTY", "ZERO", null, null, "p1");
    }

    private static Gem p2Gem() {
        return new Gem("g1", "ZERO", "FORTY", null, null, "p2");
    }
}
