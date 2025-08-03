package com.example.stratotype.mcp.data;

import java.util.List;
import java.util.Map;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for a democracy or republic.")
@Data
public class DemocracyDto implements PoliticalSystemDto {

  @Schema(description = "Name of the head of state.")
  private String headOfState;

  @Schema(description = "Type of electoral system used.")
  private String electoralSystem;

  @Schema(description = "Turn number of the last election.")
  private int lastElectionTurn;

  @Schema(description = "Turn number of the next scheduled election.")
  private int nextElectionTurn;

  @Schema(
    description = "Recent election results by party. Example: {\"Conservative Party\": 0.45, \"Socialist Front\": 0.35}"
  )
  private Map<String, Double> recentElectionResults;

  @Schema(description = "List of political parties.")
  private List<PoliticalPartyDto> politicalParties;

  @Schema(description = "List of recent opinion polls.")
  private List<OpinionPollDto> recentPolls;
}