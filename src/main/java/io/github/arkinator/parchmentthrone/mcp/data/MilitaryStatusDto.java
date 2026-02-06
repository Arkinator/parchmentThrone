package io.github.arkinator.parchmentthrone.mcp.data;

import lombok.Data;
import java.util.Map;

@Data
public class MilitaryStatusDto {
  private String nationName;
  private int armySize;
  private int navySize;
  private Map<String, Integer> unitComposition; // e.g., {"infantry": 1000, "cavalry": 250}
  private int militaryTechLevel;
  private double morale;
  private String currentLocation;
}
