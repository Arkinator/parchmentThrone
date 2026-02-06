package io.github.arkinator.parchmentthrone.genesis;

import io.github.arkinator.parchmentthrone.game.GameProperties;
import io.github.arkinator.parchmentthrone.mcp.StatusService;
import io.github.arkinator.parchmentthrone.mcp.data.GameDataDto;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorldGenesisService {

  @Autowired private GameProperties properties;

  @Value("classpath:/prompts/world-genesis-system-prompt.st")
  private Resource systemPromptResource;

  @Value("classpath:/prompts/structure-nation.st")
  private Resource structureNationResource;

  @Value("${spring.ai.openai.base-url}")
  private String openAiBaseUrl;

  @Value("${spring.ai.openai.chat.options.model}")
  private String model;

  private final StatusService mcpBasicStatus;

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    log.info("ApplicationReadyEvent empfangen. Überprüfe, ob die Weltgenerierung aktiviert ist.");

    mcpBasicStatus.updateGameData(
        GameDataDto.builder()
            .nation(properties.getStatus().getPlayerNationName())
            .currentDate(LocalDate.of(properties.getStartYear(), 1, 1).toString())
            .politicalPower(100.)
            .money(100.)
            .mentalEnergy(100.)
            .build());

    if (!properties.isGenesisEnabled()) {
      log.info("Weltgenerierung ist in 'application.yaml' deaktiviert. Überspringe den Prozess.");
      return;
    }
    new GenesisExecutor(
            properties.getStartYear(),
            properties.getStatus().getPlayerNationName(),
            mcpBasicStatus,
            systemPromptResource,
            structureNationResource,
            openAiBaseUrl,
            model)
        .execute();
  }
}
