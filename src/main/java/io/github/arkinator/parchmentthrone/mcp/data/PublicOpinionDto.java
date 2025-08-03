package io.github.arkinator.parchmentthrone.mcp.data;

import java.util.Map;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for overall public sentiment. Based on concepts like societal polarization and trust in institutions.")
@Data
public class PublicOpinionDto {
  @Schema(description = "Popular support score, from 0.0 to 1.0")
  private double popularSupportScore;

  @Schema(description = "Societal polarization, from 0.0 to 1.0, a measure of internal division")
  private double societalPolarization;

  @Schema(description = "Sentiment by region, e.g., {\"Capital\": 0.8, \"Rural North\": 0.3}")
  private Map<String, Double> sentimentByRegion;

  @Schema(description = "The primary source of public discontent (key grievances)")
  private Map<String, String> keyGrievances;
}