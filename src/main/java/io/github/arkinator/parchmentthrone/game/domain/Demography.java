package io.github.arkinator.parchmentthrone.game.domain;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder
public record Demography(
    int population,
    float growthRate,
    Map<String, Float> ageStructure,
    float urbanization,
    float migrationBalance) {
  public Demography applyDelta(Demography delta) {
    if (delta == null) {
      return this;
    }
    return Demography.builder()
        .population(this.population + delta.population())
        .growthRate(this.growthRate + delta.growthRate())
        .ageStructure(this.ageStructure) // For simplicity, not updating ageStructure here
        .urbanization(this.urbanization + delta.urbanization())
        .migrationBalance(this.migrationBalance + delta.migrationBalance())
        .build();
  }
}
