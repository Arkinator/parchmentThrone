package io.github.arkinator.parchmentthrone.game;

import static com.fasterxml.jackson.annotation.JsonInclude.*;
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

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().setSerializationInclusion(Include.NON_NULL);

  private boolean genesisEnabled;
  private int startYear;
  private String playerNationName;
  private String playerCharacterName;
  private String playerCharacterTitle;
  private String turnLength;
  private String gameEngineModel;
  private String currentDate;
  private Map<String, String> mocks;
  private Integer mockingForRounds;

  public Map<String, Object> toMap() {
    return OBJECT_MAPPER.convertValue(this, new TypeReference<>() {});
  }
}
