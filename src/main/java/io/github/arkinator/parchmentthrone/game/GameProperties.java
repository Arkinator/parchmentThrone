package io.github.arkinator.parchmentthrone.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "game")
public class GameProperties {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private boolean genesisEnabled;
  private int startYear;
  private String playerNationName;
  private String turnLength;
  private String gameEngineModel;
  private String currentDate;

  public Map<String, Object> toMap() {
    return OBJECT_MAPPER.convertValue(this, new TypeReference<>() {});
  }
}
