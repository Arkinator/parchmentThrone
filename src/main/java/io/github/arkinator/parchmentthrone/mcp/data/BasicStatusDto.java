package io.github.arkinator.parchmentthrone.mcp.data;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class BasicStatusDto {

  @Schema(description = "Name of the nation", example = "Atlantis")
  private String nationName;

  @Schema(description = "Name of the ruler", example = "King Arthur")
  private String rulerName;

  @Schema(description = "Population in millions", example = "12.5")
  private double populationInMillions;

  @Schema(description = "Technology level in years", example = "2024")
  private double technologyLevelInYears;

  @Schema(description = "Type of government", example = "Monarchy")
  private String governmentType;

  @Schema(description = "Name of the capital city", example = "Avalon")
  private String capitalCity;
}
