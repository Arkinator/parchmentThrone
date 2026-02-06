package io.github.arkinator.parchmentthrone.game;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "game")
public class GameProperties {

  private boolean genesisEnabled;
  private int startYear;
  private String turnLength;
  private String gameEngineModel;
  private String currentDate;
  private Map<String, String> mocks;
  private Integer mockingForRounds;
  private GameStatus status;
}
