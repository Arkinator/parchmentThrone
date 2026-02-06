package io.github.arkinator.parchmentthrone.game;

import io.github.arkinator.parchmentthrone.game.domain.CountryState;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "game.status")
public class GameStatus {

  private String stateJson;
  private String briefingYaml;
  private String decisionYaml;
  private String playerNationName;
  private String playerCharacterName;
  private String playerCharacterTitle;
  private String currentDate;
  private CountryState currentState;
}
