package io.github.arkinator.parchmentthrone.mcp.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Schema(description = "DTO for state of propaganda and information control. Reflects social science findings on media control and public perception.")
@Data
public class PropagandaStatusDto {
  @Schema(description = "0.0 to 1.0, how effective the state's message is")
  private double propagandaGrasp;

  @Schema(description = "0.0 to 1.0, degree of press freedom")
  private double mediaOpenness;

  @Schema(description = "Whether censorship is currently active")
  private boolean isCensorshipActive;

  @Schema(description = "What the state is currently promoting.")
  private List<String> primaryNarratives;

  @Schema(description = "What opposing groups are promoting.")
  private List<String> counterNarratives;
}