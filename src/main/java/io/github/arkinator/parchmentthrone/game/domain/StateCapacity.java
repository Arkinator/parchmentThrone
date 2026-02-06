package io.github.arkinator.parchmentthrone.game.domain;

import java.util.Map;
import lombok.Builder;

@Builder
public record StateCapacity(
    float bureaucracy, float military, float taxation, float lawEnforcement, float corruption) {
  public StateCapacity applyDelta(StateCapacity delta) {
    if (delta == null) {
      return this;
    }
    return StateCapacity.builder()
       .bureaucracy(bureaucracy + delta.bureaucracy())
        .military(military + delta.military())
        .taxation(taxation + delta.taxation())
        .lawEnforcement(lawEnforcement + delta.lawEnforcement())
        .corruption(corruption + delta.corruption())
        .build();
  }
}
