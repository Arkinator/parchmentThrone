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
  private String gameEngineInitPrompt;
  private String gameEngineStatusPrompt;
  private final List<Message> history = new ArrayList<>();

  @SneakyThrows
  @EventListener(ApplicationReadyEvent.class)
  private void onApplicationReadyEvent() {
    generateChatModel();
    this.gameEngineSystemPrompt =
        systemPromptResource.getContentAsString(Charset.defaultCharset()); // .split("------*");
    //    this.gameEngineSystemPrompt = promptParts[0].trim();
    //    this.gameEngineInitPrompt = promptParts[1].trim();
    //    this.gameEngineStatusPrompt = promptParts[2].trim();
  }

  private String renderPrompt(String rawPrompt, Map.Entry<String, String>... additionalEntries) {
    final ST st = new ST(rawPrompt);
    // add all game properties to the template using reflection
    Stream.of(gameProperties.getClass().getDeclaredFields())
        .forEach(
            field -> {
              field.setAccessible(true);
              try {
                st.add(field.getName(), field.get(gameProperties));
              } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
              }
            });
    // add additional entries to the template
    Stream.of(additionalEntries).forEach(entry -> st.add(entry.getKey(), entry.getValue()));

    return st.render();
  }

  @GetMapping("/game/start-status-report")
  @SneakyThrows
  public GameStatusDataDto getStartStatusReport() {
    val initMessage =
        new UserMessage(
            renderPrompt(
                gameEngineSystemPrompt,
                Pair.of("stateJSON", initJsonResource.getContentAsString(StandardCharsets.UTF_8))));
    history.add(initMessage);
    val initStepReply =
        chatModel
            .call(
                Prompt.builder()
                    .messages(history)
                    .chatOptions(
                        ToolCallingChatOptions.builder()
                            .model(gameProperties.getGameEngineModel())
                            .build())
                    .build())
            .getResult()
            .getOutput()
            .getText();
    log.info("Reply to init step: {}", initStepReply);
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

  @PostMapping("/game/chat")
  public GameStatusDataDto chatWithGameEngine(@RequestBody String userMessage) {
    log.info("Received user message: {}", userMessage);
    val userMsg =
        new UserMessage(
            "Reply from the user: '"
                + userMessage
                + "'"
                + "Please respond in the context of the game world. "
                + "Update the game-state and the status of the nation accordingly."
                + " If the turn is over, please indicate this by changing the date in the game state.");
    history.add(userMsg);
    val response =
        chatModel.call(
            Prompt.builder()
                .messages(history)
                .chatOptions(
                    ToolCallingChatOptions.builder()
                        .toolCallbacks(ToolCallbacks.from(statusService))
                        .model(gameProperties.getGameEngineModel())
                        .build())
                .build());
    val output = response.getResult().getOutput();
    log.info("Chat response: {}", output.getText());
    history.add(output);
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .chatMessage(output.getText())
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
