package io.github.arkinator.parchmentthrone.mcp.data;

import lombok.Data;
import java.util.List;

@Data
public class ResourceStatusDto {

  private String nationName;
  private List<ResourceDto> resources;

  @Data
  public static class ResourceDto {

    private String resourceType; // e.g., "Food", "Gold"
    private int currentStock;
    private int productionPerTurn;
    private int consumptionPerTurn;
  }
}