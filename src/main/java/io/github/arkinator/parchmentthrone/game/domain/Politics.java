package io.github.arkinator.parchmentthrone.game.domain;

import lombok.Builder;

@Builder
public record Politics(
    RegimeType regimeType,
    float legitimacy,
    float eliteCompetition,
    float inclusivity,
    float repression) {
  public enum RegimeType {
    AUTOCRACY,
    OLIGARCHY,
    DEMOCRACY,
    THEOCRACY,
    TRIBAL
  }

  public Politics applyDelta(Politics delta) {
    if (delta == null) {
      return this;
    }
    return Politics.builder()
        .regimeType(delta.regimeType != null ? delta.regimeType : regimeType)
        .legitimacy(legitimacy + delta.legitimacy())
        .eliteCompetition(eliteCompetition + delta.eliteCompetition())
        .inclusivity(inclusivity + delta.inclusivity())
        .repression(repression + delta.repression())
        .build();
  }
}
