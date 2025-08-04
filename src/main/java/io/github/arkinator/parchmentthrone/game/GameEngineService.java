package io.github.arkinator.parchmentthrone.game;

import io.github.arkinator.parchmentthrone.game.dto.GameStatsDto;
import io.github.arkinator.parchmentthrone.game.dto.GameStatusDataDto;
import io.github.arkinator.parchmentthrone.mcp.StatusService;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.stringtemplate.v4.ST;

@RestController
@Data
@Slf4j
public class GameEngineService {

  private final StatusService statusService;
  private final GameProperties gameProperties;

  @Value("classpath:/prompts/game-engine.st")
  private Resource systemPromptResource;

  @Value("classpath:/germany1931.json")
  private Resource initJsonResource;

  @Value("${spring.ai.openai.base-url}")
  private String openAiBaseUrl;

  private OpenAiChatModel chatModel;
  private String gameEngineSystemPrompt;
  private String advisorSystemPrompt;
  private GameChat statusChat;
  private GameChat advisorChat;

  @SneakyThrows
  @EventListener(ApplicationReadyEvent.class)
  private void onApplicationReadyEvent() {
    generateChatModel();
    this.gameEngineSystemPrompt =
        systemPromptResource.getContentAsString(Charset.defaultCharset()).split("------*")[0];
    this.advisorSystemPrompt =
        systemPromptResource.getContentAsString(Charset.defaultCharset()).split("------*")[1];
    statusService.updateNationStatus(
        "germany", initJsonResource.getContentAsString(StandardCharsets.UTF_8));
  }

  @GetMapping("/game/start-status-report")
  @SneakyThrows
  public GameStatusDataDto getStartStatusReport() {
    statusChat =
        new GameChat(
            chatModel,
            gameProperties,
            statusService,
            ToolCallingChatOptions.builder()
                .toolCallbacks(ToolCallbacks.from(statusService, this))
                .model(gameProperties.getGameEngineModel())
                .build());
    advisorChat =
        new GameChat(
            chatModel,
            gameProperties,
            statusService,
            ToolCallingChatOptions.builder()
                .toolCallbacks(ToolCallbacks.from(statusService, this))
                .model(gameProperties.getGameEngineModel())
                .build());
    val initStepReply = statusChat.sendMessage(gameEngineSystemPrompt);
    /*    val statusPrompt = new UserMessage(renderPrompt(gameEngineStatusPrompt));
    history.add(statusPrompt);
    val statusStepReply = chatModel.call(Prompt.builder().messages(history)
        .chatOptions(ChatOptions.builder().model(gameProperties.getGameEngineModel()).build()).build()).getResult()
      .getOutput().getText();
    log.info("Reply to status step: {}", statusStepReply);
    return statusStepReply;*/
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .summaryHtml(initStepReply)
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

  @Tool(description = "gives a short brief of the projects and plans for the current turn to the advisor")
  public void advisorBrief(String briefing) {
    log.info("Briefing the advisor about the current turn projects and plans: {}", briefing);
//    val output = advisorChat.sendMessage(briefing);
//    log.info("Chat response to briefing: {}", output);
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

  private void generateChatModel() {
    OpenAiApi openAiApi =
        OpenAiApi.builder()
            .baseUrl(openAiBaseUrl)
            .apiKey(new NoopApiKey())
            .webClientBuilder(
                WebClient.builder()
                    .clientConnector(
                        new JdkClientHttpConnector(
                            HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_1_1)
                                .connectTimeout(Duration.ofSeconds(30))
                                .build())))
            .restClientBuilder(
                RestClient.builder()
                    .requestFactory(
                        new JdkClientHttpRequestFactory(
                            HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_1_1)
                                .connectTimeout(Duration.ofSeconds(30))
                                .build())))
            .build();
    var openAiChatOptions =
        OpenAiChatOptions.builder()
            .model(gameProperties.getGameEngineModel())
            .temperature(0.4)
            .build();
    chatModel =
        OpenAiChatModel.builder().defaultOptions(openAiChatOptions).openAiApi(openAiApi).build();
  }
}
