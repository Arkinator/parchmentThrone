package io.github.arkinator.parchmentthrone.mcp.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameDataDto {

  private String currentDate;
  private String nation;
  private double money;
  private double politicalPower;
  private double mentalEnergy;
}
