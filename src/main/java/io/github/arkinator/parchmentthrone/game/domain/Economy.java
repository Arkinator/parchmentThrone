package io.github.arkinator.parchmentthrone.game.domain;

import lombok.Builder;

@Builder
public record Economy(
    float gdp,
    float inequality,
    float productivity,
    float eliteSurplus,
    float subsistenceSecurity
) {
public Economy applyDelta(Economy delta) {
    if (delta == null) {
        return this;
    }
    return Economy.builder()
        .gdp(gdp + delta.gdp())
        .inequality(inequality + delta.inequality())
        .productivity(productivity + delta.productivity())
        .eliteSurplus(eliteSurplus + delta.eliteSurplus())
        .subsistenceSecurity(subsistenceSecurity + delta.subsistenceSecurity())
        .build();
}}