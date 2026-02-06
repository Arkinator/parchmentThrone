package io.github.arkinator.parchmentthrone.game.dto;

import io.github.arkinator.parchmentthrone.game.domain.CountryState;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameProjectEntity {
  String projectName;
  String description;
  double estimatedCostGold;
  int estimatedCostPoliticalPower;
  ProjectStatus status;
  String startDate;
  String endDate;
  double completionPercentage;
  CountryState impacts;

  public CountryState applyTo(CountryState state) {
    if (impacts == null) {
      return state;
    }
    return state.applyDelta(impacts);
  }
}
