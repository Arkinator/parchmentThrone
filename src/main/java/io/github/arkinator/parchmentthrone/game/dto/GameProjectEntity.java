package io.github.arkinator.parchmentthrone.game.dto;

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
}
