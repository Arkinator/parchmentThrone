package io.github.arkinator.parchmentthrone.game;

import static io.github.arkinator.parchmentthrone.utils.BeanUtils.toMap;

import io.github.arkinator.parchmentthrone.game.dto.GameStatsDto;
import io.github.arkinator.parchmentthrone.game.dto.GameStatusDataDto;
import io.github.arkinator.parchmentthrone.mcp.StatusService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.openai.api.ResponseFormat.Type;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
  private final ProjectService projectService;

  @Value("classpath:/prompts/event-bot.st")
  private Resource eventBotPrompt;

  @Value("classpath:/prompts/briefing-bot.st")
  private Resource briefingBotPrompt;

  @Value("classpath:/prompts/game-engine.st")
  private Resource gameEnginePrompt;

  @Value("classpath:/germany1931.json")
  private Resource initJsonResource;

  @Value("${spring.ai.openai.base-url}")
  private String openAiBaseUrl;

  @Autowired private ChatClient chatClient;
  @Autowired private AdvisorService advisorService;
  @Autowired private ChatModel chatModel;
  @Autowired private final VectorStore vectorStore;

  private int turnCounter = 0;
  @Autowired private GameStatus gameStatus;

  @GetMapping("/game/fetch-status-report")
  @SneakyThrows
  public GameStatusDataDto getStatusReport() {
    final String briefingYaml =
        chooseMockResponse(
            this::generateBriefingYaml, gameProperties.getStatus().getBriefingYaml(), "briefing");
    final String htmlBriefing =
        chooseMockResponse(
            () -> generateHtmlBriefing(briefingYaml), "some briefing HTML here", "html");
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .summaryHtml(htmlBriefing)
        .chatMessage(advisorService.generateWelcomeMessage())
        .build();
  }

  private String generateHtmlBriefing(String briefingYaml) {
    val htmlBriefingResponse =
        chatClient
            .prompt()
            .user(
                spec ->
                    spec.text(briefingBotPrompt)
                        .params(generatePlaceholderMap())
                        .param("briefingYAML", briefingYaml))
            .call()
            .chatResponse();
    log.info("htmlBriefing generated");
    return htmlBriefingResponse.getResult().getOutput().getText();
  }

  private String generateBriefingYaml() {
    log.info("Generating briefing yaml...");
    final CallResponseSpec responseSpec = ChatClient.create(this.chatModel)
      .prompt()
      .user(spec -> spec.text(eventBotPrompt).params(generatePlaceholderMap()))
      .tools(projectService)
      .call();
    val briefingYaml =
        responseSpec
            .content();

    log.info("briefingResult YAML generated: {}", briefingYaml);
    return briefingYaml;
  }

  private Map<String, Object> generatePlaceholderMap() {
    final HashMap<String, Object> result = new HashMap<>();
    result.putAll(toMap(gameProperties));
    result.putAll(toMap(gameStatus));
    return result;
  }

  private String chooseMockResponse(Supplier<String> supplier, String fallback, String mockType) {
    if (Optional.ofNullable(gameProperties.getMocks().get(mockType))
            .map(Boolean::valueOf)
            .orElse(false)
        && Optional.ofNullable(gameProperties.getMockingForRounds())
            .filter(mr -> mr > turnCounter)
            .isPresent()) {
      log.info("Mock mode is enabled, returning mock response for type: {}", mockType);
      return fallback;
    }
    log.info(
        "Mock mode is disabled or not applicable, generating response for type {} and round {}",
        mockType,
        turnCounter);
    return supplier.get();
  }

  @PostMapping("/game/end-turn")
  public GameStatusDataDto endTurn() {
    log.info("Ending current turn!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    val output = advisorService.generateDecisionsYaml();
    turnCounter++;
    val nextTurnJson =
        chatClient
            .prompt()
//            .options(
//                OpenAiChatOptions.builder()
//                    .responseFormat(ResponseFormat.builder().type(Type.JSON_SCHEMA).build())
//                    .build())
            .user(
                spec ->
                    spec.text(gameEnginePrompt)
                        .params(generatePlaceholderMap())
                        .param("decisionsYaml", output))
            .call()
            .chatResponse();
    gameProperties.getStatus().setStateJson(nextTurnJson.getResult().getOutput().getText());
    log.info("updated JSON: \n{}", gameProperties.getStatus().getStateJson());
    return getStatusReport();
  }

  @PostMapping("/game/chat")
  public GameStatusDataDto chatWithGameEngine(@RequestBody String userMessage) {
    log.info("Received user message: {}", userMessage);
    final var response = advisorService.interactWithAdvisor(userMessage);
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .chatMessage(response)
        .build();
  }
}
