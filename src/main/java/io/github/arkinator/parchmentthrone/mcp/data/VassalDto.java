package io.github.arkinator.parchmentthrone.mcp.data;

import java.util.List;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class VassalDto {

  @Schema(description = "The name of the vassal or noble house, e.g., 'House of Blackwood'")
  private String vassalName;

  @Schema(description = "The region or territory they control")
  private String territory;

  @Schema(description = "The individual who leads the vassal. This reuses the KeyFigureDto.")
  private KeyFigureDto leader;

  @Schema(description = "A score representing their relative power (e.g., military strength, wealth)")
  private double powerScore;

  @Schema(description = "A score representing their loyalty to the monarch (e.g., 0.0 to 1.0)")
  private double loyaltyScore;

  @Schema(description = "A list of issues they are concerned about or demanding.")
  private List<PoliticalIssueDto> keyGrievances;

  @Schema(description = "Flag to indicate if the vassal is in open rebellion.")
  private boolean isRebelling;
}