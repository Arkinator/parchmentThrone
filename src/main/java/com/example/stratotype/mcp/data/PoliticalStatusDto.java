package com.example.stratotype.mcp.data;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO representing the current political status of a nation.")
@Data
public class PoliticalStatusDto {

  @Schema(description = "Name of the nation.")
  private String nationName;

  @Schema(description = "AI-generated narrative summary of the current political climate.")
  private String summaryText;

  @Schema(description = "DTO for the government type.")
  private PoliticalSystemDto politicalSystem;

  @Schema(description = "DTO representing public opinion.")
  private PublicOpinionDto publicOpinion;

  @Schema(description = "DTO representing the state of information control.")
  private PropagandaStatusDto propagandaStatus;

  @Schema(description = "List of key issues driving public discourse.")
  private List<PoliticalIssueDto> currentIssues;

  @Schema(description = "The turn number when this status was last updated.")
  private int lastUpdatedTurn;
}