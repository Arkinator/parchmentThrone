package io.github.arkinator.parchmentthrone.genesis;

import io.github.arkinator.parchmentthrone.mcp.McpBasicStatus;
import io.github.arkinator.parchmentthrone.mcp.McpStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorldGenesisService {

  private final WorldGenesisProperties properties;

  @Value("classpath:/prompts/world-genesis-system-prompt.st")
  private Resource systemPromptResource;

  private final McpStatusService mcpStatusService;
  private final McpBasicStatus mcpBasicStatus;

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    log.info("ApplicationReadyEvent empfangen. Überprüfe, ob die Weltgenerierung aktiviert ist.");

    if (!properties.isEnabled()) {
      log.info("Weltgenerierung ist in 'application.yaml' deaktiviert. Überspringe den Prozess.");
      return;
    }
    new GenesisExecutor(
      properties.getStartYear(),
      properties.getPlayerNationName(),
      mcpBasicStatus,
      systemPromptResource
    ).execute();
  }
}