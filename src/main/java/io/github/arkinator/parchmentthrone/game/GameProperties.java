package io.github.arkinator.parchmentthrone.game;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "game")
public class GameProperties {

  private boolean genesisEnabled;
  private int startYear;
  private String playerNationName;
  private String turnLength;
  private String gameEngineModel;
  private String currentDate;
}