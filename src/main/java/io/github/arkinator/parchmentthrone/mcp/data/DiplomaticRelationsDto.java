package io.github.arkinator.parchmentthrone.mcp.data;

import lombok.Data;
import java.util.List;

@Data
public class DiplomaticRelationsDto {
  private String nationName;
  private List<RelationDto> relations;

  @Data
  public static class RelationDto {
    private String otherNationName;
    private String relationType; // e.g., "Alliance", "Rivalry"
    private int opinionScore; // -100 to 100
    private int lastInteractionTurn;
  }
}