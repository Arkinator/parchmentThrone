package io.github.arkinator.parchmentthrone.game;

import static io.github.arkinator.parchmentthrone.utils.BeanUtils.toMap;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvisorService {

  private final GameProperties gameProperties;
  private final GameStatus gameStatus;
  private final ChatClient chatClient;
  private final ProjectService projectService;
  private GameChat advisorChat;

  @Value("classpath:/prompts/advisor-bot.st")
  private Resource advisorBotPrompt;

  @SneakyThrows
  @EventListener(ApplicationReadyEvent.class)
  private void onApplicationReadyEvent() {
    final HashMap<String, String> placeholders = new HashMap<>();
    advisorChat =
        new GameChat(chatClient, gameProperties, projectService, advisorBotPrompt, placeholders);
  }

  public String generateDecisionsYaml() {
    updatePlaceholders();
    final String output =
        advisorChat.sendMessage(
            "The turn is over. Please give a short summary of the chosen projects. Please reflect accurately the "
                + "decisions made by the player in this turn. "
                + "The summary should be in YAML format, with the following structure: "
                + "projects: [project1, project2, ...]. "
                + "Return the summary only in YAML format, no other text. "
                + "Optimize it for another AI to evaluate.");
    log.info("Chat response to JSON update: {}", output);
    gameStatus.setDecisionYaml(output);
    advisorChat.sendMessage(
        "Now update the projects via tool calls based on the player's decisions. Do only update new projects, do not update existing ones.");
    return output;
  }

  public String generateWelcomeMessage() {
    updatePlaceholders();
    return advisorChat.initialize();
  }

  public String interactWithAdvisor(String userMessage) {
    val response =
        advisorChat.sendMessage(
            "Reply from the player: '"
                + userMessage
                + "'"
                + "Please respond in the context of the game world. "
                + "Engage in a dialog with the player, possibly answering his questions, working on details of the proposals.");
    log.info("Chat response: {}", response);
    return response;
  }

  private void updatePlaceholders() {
    advisorChat.getPlaceholders().put("briefingYAML", gameStatus.getBriefingYaml());
    addPropertiesToPlaceholders(gameProperties);
    addPropertiesToPlaceholders(gameStatus);
  }

  private void addPropertiesToPlaceholders(Object target) {
    toMap(target).entrySet().stream()
        .filter(e -> !Objects.isNull(e.getValue()))
        .forEach(
            entry ->
                advisorChat.getPlaceholders().put(entry.getKey(), entry.getValue().toString()));
  }
}
