package io.github.arkinator.parchmentthrone.mcp.data;

import java.util.List;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class MonarchyDto implements PoliticalSystemDto {
  @Schema(description = "Name of the current ruler")
  private String rulerName;

  @Schema(description = "Succession law, e.g., 'Hereditary', 'Elective'")
  private String successionLaw;

  @Schema(description = "Monarch's influence, 0.0 to 1.0, representing power from absolute to constitutional")
  private double monarchInfluence;

  @Schema(description = "Name of the ruling dynasty")
  private String rulingDynasty;

  @Schema(description = "Key noble houses or powerful figures")
  private List<VassalDto> influentialVassals;
}