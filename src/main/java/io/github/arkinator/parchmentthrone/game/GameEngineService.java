package io.github.arkinator.parchmentthrone.game;

import io.github.arkinator.parchmentthrone.game.dto.GameStatsDto;
import io.github.arkinator.parchmentthrone.game.dto.GameStatusDataDto;
import io.github.arkinator.parchmentthrone.mcp.StatusService;
import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
@Slf4j
public class GameEngineService {

  private final StatusService statusService;
  private final GameProperties gameProperties;

  @Value("classpath:/prompts/event-bot.st")
  private Resource eventBotPrompt;

  @Value("classpath:/prompts/briefing-bot.st")
  private Resource briefingBotPrompt;

  @Value("classpath:/prompts/advisor-bot.st")
  private Resource advisorBotPrompt;

  @Value("classpath:/germany1931.json")
  private Resource initJsonResource;

  @Value("${spring.ai.openai.base-url}")
  private String openAiBaseUrl;

  @Autowired private ChatModel chatModel;

  private String stateJson;
  private GameChat advisorChat;

  @SneakyThrows
  @EventListener(ApplicationReadyEvent.class)
  private void onApplicationReadyEvent() {
    this.stateJson = IOUtils.toString(initJsonResource.getInputStream(), StandardCharsets.UTF_8);
    advisorChat =
        new GameChat(
            chatModel,
            gameProperties,
            statusService,
            ToolCallingChatOptions.builder()
                //                .toolCallbacks(ToolCallbacks.from(statusService, this))
                .model(gameProperties.getGameEngineModel())
                .build(),
            advisorBotPrompt,
            new HashMap<>());
  }

  @GetMapping("/game/start-status-report")
  @SneakyThrows
  public GameStatusDataDto getStartStatusReport() {
    log.info("Generating start status report for the game.");
    val briefingResult =
        ChatClient.create(chatModel)
            .prompt()
            .user(
                spec ->
                    spec.text(eventBotPrompt)
                        .params(gameProperties.toMap())
                        .param("stateJSON", stateJson))
            .call()
            .chatResponse();
    log.info("briefingResult YAML generated: {}", briefingResult.getResult().getOutput().getText());
    val htmlBriefing =
        ChatClient.create(chatModel)
            .prompt()
            .user(
                spec ->
                    spec.text(briefingBotPrompt)
                        .params(gameProperties.toMap())
                        .param("briefingYAML", briefingResult.getResult().getOutput().getText()))
            .call()
            .chatResponse();
    log.info("htmlBriefing generated");
    advisorChat.getPlaceholders().put("stateJSON", stateJson);
    advisorChat
        .getPlaceholders()
        .put("briefingYAML", briefingResult.getResult().getOutput().getText());
    gameProperties
        .toMap()
        .forEach((key, value) -> advisorChat.getPlaceholders().put(key, value.toString()));
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .summaryHtml(htmlBriefing.getResult().getOutput().getText())
        .chatMessage(advisorChat.initialize())
        .build();
  }

  @Tool(description = "Ends the current turn")
  public void endTurn() {
    log.info("Ending current turn!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    val output =
        advisorChat.sendMessage(
            "The turn is over. Lets update the game state and the status of the nation accordingly. "
                + "Please use the format of the current game state JSON to update the game state. The current game state is as follows:\n<stateJSON>");
    log.info("Chat response to JSON update: {}", output);
  }

  @PostMapping("/game/chat")
  public GameStatusDataDto chatWithGameEngine(@RequestBody String userMessage) {
    log.info("Received user message: {}", userMessage);
    val response =
        advisorChat.sendMessage(
            "Reply from the player: '"
                + userMessage
                + "'"
                + "Please respond in the context of the game world. "
                + "Update the game-state and the status of the nation accordingly."
                + "Engage in a dialog with the player, possibly answering his questions, working on details of the proposals. "
                + "Only end the turn if the user actively expresses his desire to do so. Do so by calling the 'endTurn' tool. ");
    log.info("Chat response: {}", response);
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .chatMessage(response)
        .build();
  }
}
