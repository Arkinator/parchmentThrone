package io.github.arkinator.parchmentthrone.game.domain;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import lombok.Builder;

@Builder
public record CountryHistory(
    Deque<CountryState> recentHistory, String narrativeSummary, List<CountryState> snapshots) {
/*
  public void update(CountryState state, List<Effect> effects) {
    recentHistory.addLast(state.snapshot());
    if (recentHistory.size() > 10) recentHistory.removeFirst();
    snapshots.add(state.snapshot());
    // narrativeSummary update is a placeholder
  }

  public List<Float> getTrend(String variableName) {
    List<Float> trend = new ArrayList<>();
    for (CountryState cs : recentHistory) {
      // Reflection or manual mapping needed for real implementation
      // Placeholder: returns empty
    }
    return trend;
  }*/
}
