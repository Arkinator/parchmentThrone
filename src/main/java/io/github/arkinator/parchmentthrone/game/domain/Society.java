package io.github.arkinator.parchmentthrone.game.domain;

import lombok.Builder;

@Builder
public record Society(
    float cohesion,
    float unrest,
    float trustInInstitutions,
    float culturalDiversity,
    float religionInfluence) {
  public Society applyDelta(Society delta) {
    if (delta == null) {
      return this;
    }
    return Society.builder()
        .cohesion(cohesion + delta.cohesion())
        .unrest(unrest + delta.unrest())
        .trustInInstitutions(trustInInstitutions + delta.trustInInstitutions())
        .culturalDiversity(culturalDiversity + delta.culturalDiversity())
        .religionInfluence(religionInfluence + delta.religionInfluence())
        .build();
  }
}
