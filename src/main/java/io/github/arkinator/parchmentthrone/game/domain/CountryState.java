package io.github.arkinator.parchmentthrone.game.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CountryState {
  private final Demography demography;
  private final Economy economy;
  private final StateCapacity stateCapacity;
  private final Society society;
  private final Politics politics;
  private final Environment environment;

  public CountryState applyDelta(CountryState delta) {
    return this.toBuilder()
        .demography(demography.applyDelta(delta.getDemography()))
        .economy(economy.applyDelta(delta.getEconomy()))
        .stateCapacity(stateCapacity.applyDelta(delta.getStateCapacity()))
        .society(society.applyDelta(delta.getSociety()))
        .politics(politics.applyDelta(delta.getPolitics()))
        .environment(environment.applyDelta(delta.getEnvironment()))
        .build();
  }
}
