package io.github.arkinator.parchmentthrone.mcp.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;

@Data
public class OpinionPollDto {
  @Schema(description = "Polling organization conducting the poll")
  private String pollingOrganization;

  @Schema(description = "Turn number of the poll")
  private int turnNumber;

  @Schema(
      description =
          "Poll results by party, e.g., {\"Conservative Party\": 0.42, \"Socialist Front\": 0.38}")
  private Map<String, Double> results;

  @Schema(description = "Polling methodology, e.g., \"Online Survey\", \"Phone Interviews\"")
  private String methodology;
}
