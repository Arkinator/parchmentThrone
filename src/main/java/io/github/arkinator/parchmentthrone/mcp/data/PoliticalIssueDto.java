package io.github.arkinator.parchmentthrone.mcp.data;

import java.util.Map;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class PoliticalIssueDto {
  @Schema(description = "Name of the political issue")
  private String issueName;

  @Schema(description = "Description of the political issue")
  private String description;

  @Schema(description = "Public sentiment, e.g., 'Highly Contentious', 'Largely Ignored'")
  private String publicSentiment;

  @Schema(description = "Faction support map, e.g., {\"Peasants\": 0.9, \"Nobility\": 0.2}")
  private Map<String, Double> factionSupport;
}