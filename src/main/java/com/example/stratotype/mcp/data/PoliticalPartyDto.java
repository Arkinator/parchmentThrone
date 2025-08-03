package com.example.stratotype.mcp.data;

import java.util.List;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reusable DTOs for components that can be part of many systems.")
@Data
public class PoliticalPartyDto {
  @Schema(description = "Name of the political party")
  private String partyName;

  @Schema(description = "Name of the party leader")
  private String leaderName;

  @Schema(description = "Core ideology of the party")
  private String coreIdeology;

  @Schema(description = "Key platform issues of the party")
  private String keyPlatformIssues;

  @Schema(description = "List of key figures in the party")
  private List<KeyFigureDto> keyFigures;
}