package io.github.arkinator.parchmentthrone.genesis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "game.world-genesis")
public class WorldGenesisProperties {

  private boolean enabled;
  private int startYear;
  private String playerNationName;
}