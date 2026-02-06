package io.github.arkinator.parchmentthrone.mcp.data;

import java.util.Map;
import lombok.Data;

@Data
public class InternalStabilityDto {
  private String nationName;
  private double stabilityScore; // 0.0 to 1.0
  private int unrestLevel; // 0-10
  private Map<String, Integer> rebelFactions; // e.g., {"Peasant Rebellion": 5000}
  private int recentRiots;
}
