package io.github.arkinator.parchmentthrone.game.domain;

import lombok.Builder;

@Builder
public record Environment(
    float climateStress, float pollution, float arableLand, float resourceBase) {
  public Environment applyDelta(Environment delta) {
    if (delta == null) {
      return this;
    }
    return Environment.builder()
        .arableLand(arableLand + delta.arableLand())
        .resourceBase(resourceBase + delta.resourceBase())
        .climateStress(climateStress + delta.climateStress())
        .pollution(pollution + delta.pollution())
        .build();
  }
}
