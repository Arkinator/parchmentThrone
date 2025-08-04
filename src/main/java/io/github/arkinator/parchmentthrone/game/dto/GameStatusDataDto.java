package io.github.arkinator.parchmentthrone.game.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GameStatusDataDto {
  String summaryHtml;
  String chatMessage;
  GameStatsDto stats;
}
