package com.tei.tennis.point.gatewayservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Set {
    private String setId;
    private String winner;
    private List<Gem> gems;
}
