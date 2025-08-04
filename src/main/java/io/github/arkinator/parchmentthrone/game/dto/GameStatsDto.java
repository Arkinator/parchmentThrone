package io.github.arkinator.parchmentthrone.game.dto;

import io.github.arkinator.parchmentthrone.mcp.data.GameDataDto;
import lombok.Value;

@Value
public class GameStatsDto {
  double money;
  double politicalPower;
  double mentalEnergy;

  public static GameStatsDto create(GameDataDto data) {
    return new GameStatsDto(data.getMoney(), data.getPoliticalPower(), data.getMentalEnergy());
  }
}
