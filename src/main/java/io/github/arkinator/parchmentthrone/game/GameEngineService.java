package io.github.arkinator.parchmentthrone.game;

import io.github.arkinator.parchmentthrone.game.dto.GameStatsDto;
import io.github.arkinator.parchmentthrone.game.dto.GameStatusDataDto;
import io.github.arkinator.parchmentthrone.mcp.StatusService;
import io.micrometer.core.instrument.util.IOUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.openai.api.ResponseFormat.Type;
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

  private static final String BRIEFING_YAML =
"""
briefing:
  date: 1.1.1931
  resourceStatus:
    politicalPowerGainRate: 1.2
    politicalPower: 48.7
    goldSpent: 12500000
    goldFromTaxationThisTurn: 18700000
    goldAfterSpendingAndTaxation: 6200000
  ongoingProjects:
    - name: "Reichsbank Modernization Initiative"
      status: "In progress"
      estimatedTimeUntilCompletion: "6 months"
      costThisTurn: 1500000
  dailyHerald:
    headline: "Rising Unrest in Berlin as Unemployment Surpasses 18%"
    content: "A wave of strikes has swept across industrial centers in the Ruhr Valley, with workers demanding wage increases and better conditions. The SPD warns that the government’s austerity measures are fueling social unrest, while the NSDAP holds mass rallies in Dresden and Hamburg, promising to 'restore German dignity.' Chancellor Brüning insists that fiscal responsibility must be maintained, but pressure is mounting from all sides."
  upcomingElections:
    - electionDate: 1932.05.13
      candidates:
        - name: "Paul von Hindenburg"
          party: "Independent (Presidential)"
          pollingResults: 0.41
        - name: "Adolf Hitler"
          party: "NSDAP"
          pollingResults: 0.32
        - name: "Otto Wels"
          party: "SPD"
          pollingResults: 0.15
  events:
    - description: "A major strike at the Krupp steelworks in Essen has paralyzed heavy industry output for two weeks. Workers are demanding a 20% wage increase and recognition of union rights. The government has refused to negotiate, citing budget constraints."
      impact: "severe"
      charactersInvolved:
        - name: "Heinrich Brüning"
          role: "Chancellor"
          quote: "We cannot afford to capitulate to labor demands. The Young Plan must be honored, and the state must remain fiscally responsible."
    - description: "The NSDAP has gained 12 new seats in regional elections in Saxony and Thuringia, consolidating its position as the largest party in the Reichstag. Hitler has declared that 'the time of the old order is over.'"
      impact: "severe"
      charactersInvolved:
        - name: "Adolf Hitler"
          role: "NSDAP Leader"
          quote: "The German people have spoken. The age of compromise is over. We will lead, or we will destroy."
    - description: "A severe winter storm has damaged rail lines in northern Germany, disrupting grain transport from the Baltic provinces. Food shortages are feared in urban centers."
      impact: "moderate"
      charactersInvolved:
        - name: "Ludwig Kaas"
          role: "Zentrumspartei Leader"
          quote: "The state must act swiftly to prevent famine. We cannot let the people suffer while the Reichstag debates."
  projects:
    - character: "Heinrich Brüning"
      projectName: "National Employment Stabilization Program"
      description: "Launch a large-scale public works initiative to reduce unemployment by constructing 500 new roads and bridges across rural Germany, funded by emergency loans from the Reichsbank."
      estimatedCostGold: 25000000
      estimatedCostPoliticalPower: 8.5
    - character: "Ernst Thälmann"
      projectName: "Workers’ Housing and Social Welfare Expansion"
      description: "Establish a network of state-funded housing for industrial workers, paired with free healthcare and education. Aimed at winning labor support and undermining the appeal of extremist parties."
      estimatedCostGold: 30000000
      estimatedCostPoliticalPower: 10.2
    - character: "Alfred Hugenberg"
      projectName: "National Industrial Revival Fund"
      description: "Create a state-backed investment fund to boost domestic heavy industry and reduce reliance on foreign capital. Prioritizes German-owned firms and protects national economic sovereignty."
      estimatedCostGold: 20000000
      estimatedCostPoliticalPower: 7.0
    - character: "Ludwig Kaas"
      projectName: "Catholic Social Integration Initiative"
      description: "Partner with the Church to launch a nationwide program of vocational training and moral education in schools and factories, aiming to counter radical ideologies through cultural cohesion."
      estimatedCostGold: 15000000
      estimatedCostPoliticalPower: 6.3
    - character: "Otto Wels"
      projectName: "Reichstag Transparency and Accountability Reform"
      description: "Introduce new laws to increase parliamentary oversight of the government, including mandatory publication of all cabinet decisions and budget justifications."
      estimatedCostGold: 8000000
      estimatedCostPoliticalPower: 5.1
    - character: "Paul von Hindenburg"
      projectName: "National Emergency Defense Council"
      description: "Establish a permanent emergency council with full authority to override parliamentary decisions during crises, ensuring swift action in times of national emergency."
      estimatedCostGold: 12000000
      estimatedCostPoliticalPower: 9.4
    - character: "Adolf Hitler"
      projectName: "German Youth Mobilization and National Awakening Campaign"
      description: "Launch a nationwide youth program to instill national pride, militarism, and loyalty to the Führer. Includes mass rallies, propaganda films, and mandatory youth training."
      estimatedCostGold: 18000000
      estimatedCostPoliticalPower: 12.0
""";

  private static String DECISION_YAML =
"""
- action_summary: "The ruler has approved two major initiatives to stabilize the domestic situation and counter rising extremism: (1) The National Employment Stabilization Program, a public works initiative to reduce unemployment by constructing 500 roads and bridges across rural Germany, funded by emergency loans from the Reichsbank; and (2) The German Home Initiative, a state-led housing and social welfare expansion program to build 20,000 homes in the Ruhr region, in partnership with the Zentrum and Catholic Church. Both programs are to be announced simultaneously in a press conference on 2.1.1931, framed as acts of national renewal. The Reichsbank’s emergency credit line will be used to cover 55 million gold in costs, with defense spending preserved. The Reichstag Transparency and Accountability Reform will be leveraged to bolster credibility. The President’s emergency authority under Article 48 will be invoked to bypass parliamentary delays. The press conference will be used to project decisive leadership, with the headline: 'The Reich has spoken. The people will work, and they will have a home.'"
- economic_impact:
  gold_spent: 55000000
  gold_after_turn: 6200000
  public_debt_as_gdp_percent: 45.9
  inflation_rate: 6.7
- political_impact:
  political_power: 48.7
  political_power_gain_rate: 1.2
  popularity_score:
    spd: 0.25
    kpd: 0.18
    z: 0.21
    dnvp: 0.28
    nsdap: 0.32
- military:
  defense_budget_usd: 690000000
  budget_as_gdp_percent: 7.3
- domestic_factions:
  industrialists_and_big_business: -0.12
  trade_unions_and_labor_movement: 0.35
  catholic_church: -0.18
- ongoing_projects:
  - name: "Reichsbank Modernization Initiative"
    status: "Completed"
    cost: 1500000
- events:
  - description: "The Reich announces the National Employment Stabilization Program and the German Home Initiative in a major press conference. The government claims the programs will reduce unemployment and restore national dignity."
    impact: "severe"
    status: "resolved"
- projects:
  - name: "National Employment Stabilization Program"
    status: "Launched"
    cost: 25000000
    political_power_cost: 8.5
  - name: "Workers’ Housing and Social Welfare Expansion"
    status: "Launched"
    cost: 30000000
    political_power_cost: 10.2
- diplomacy:
  alliances:
    - nation_id: "ITA"
      type: "strategic"
      strength: 0.65
    - nation_id: "HUN"
      type: "major_power"
      strength: 0.82
  rivals:
    - nation_id: "FRA"
      type: "great_power_rivalry"
      strength: -0.91
    - nation_id: "POL"
      type: "border dispute rivalry"
      strength: -0.75
- elections:
  - election_date: "1932.05.13"
    type: "presidential"
    status: "active"
    candidates:
      - name: "Paul von Hindenburg"
        party: "Independent (Presidential)"
        polling_results: 0.41
      - name: "Adolf Hitler"
        party: "NSDAP"
        polling_results: 0.32
      - name: "Otto Wels"
        party: "SPD"
        polling_results: 0.15
- status: "turn_complete""";

  private final StatusService statusService;
  private final GameProperties gameProperties;

  @Value("classpath:/prompts/event-bot.st")
  private Resource eventBotPrompt;

  @Value("classpath:/prompts/briefing-bot.st")
  private Resource briefingBotPrompt;

  @Value("classpath:/prompts/advisor-bot.st")
  private Resource advisorBotPrompt;

  @Value("classpath:/prompts/game-engine.st")
  private Resource gameEnginePrompt;

  @Value("classpath:/germany1931.json")
  private Resource initJsonResource;

  @Value("${spring.ai.openai.base-url}")
  private String openAiBaseUrl;

  @Autowired private ChatClient chatClient;

  private String stateJson;
  private GameChat advisorChat;
  private int turnCounter = 0;

  @SneakyThrows
  @EventListener(ApplicationReadyEvent.class)
  private void onApplicationReadyEvent() {
    this.stateJson = IOUtils.toString(initJsonResource.getInputStream(), StandardCharsets.UTF_8);
    advisorChat =
        new GameChat(
            chatClient,
            gameProperties,
            statusService,
            ToolCallingChatOptions.builder().build(),
            advisorBotPrompt,
            new HashMap<>(
                Map.of(
                    "stateJSON",
                    stateJson,
                    "briefingYAML",
                    BRIEFING_YAML,
                    "decisionsYAML",
                    DECISION_YAML)));
  }

  @GetMapping("/game/start-status-report")
  @SneakyThrows
  public GameStatusDataDto getStatusReport() {
    final String briefingYaml =
        chooseMockResponse(this::generateBriefingYaml, BRIEFING_YAML, "briefing");
    final String htmlBriefing =
        chooseMockResponse(
            () -> generateHtmlBriefing(briefingYaml), "some briefing HTML here", "html");
    advisorChat.getPlaceholders().put("stateJSON", stateJson);
    advisorChat.getPlaceholders().put("briefingYAML", briefingYaml);
    gameProperties.toMap().entrySet().stream()
        .filter(e -> !Objects.isNull(e.getValue()))
        .forEach(
            entry ->
                advisorChat.getPlaceholders().put(entry.getKey(), entry.getValue().toString()));
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .summaryHtml(htmlBriefing)
        .chatMessage(advisorChat.initialize())
        .build();
  }

  private String generateHtmlBriefing(String briefingYaml) {
    val htmlBriefingResponse =
        chatClient
            .prompt()
            .user(
                spec ->
                    spec.text(briefingBotPrompt)
                        .params(gameProperties.toMap())
                        .param("briefingYAML", briefingYaml))
            .call()
            .chatResponse();
    log.info("htmlBriefing generated");
    final String htmlBriefing = htmlBriefingResponse.getResult().getOutput().getText();
    return htmlBriefing;
  }

  private String generateBriefingYaml() {
    log.info("Generating briefing yaml...");
    val briefingResult =
        chatClient
            .prompt()
            .user(
                spec ->
                    spec.text(eventBotPrompt)
                        .params(gameProperties.toMap())
                        .param("stateJSON", stateJson))
            .call()
            .chatResponse();
    final String briefingYaml = briefingResult.getResult().getOutput().getText();
    log.info("briefingResult YAML generated: {}", briefingYaml);
    return briefingYaml;
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
    log.info("Mock mode is disabled or not applicable, generating response for type {} and round {}", mockType, turnCounter);
    try {
      return supplier.get();
    } catch (Exception e) {
      log.error("Failed to generate response, using fallback: {}", e.getMessage());
      return fallback;
    }
  }

  @PostMapping("/game/end-turn")
  public GameStatusDataDto endTurn() {
    log.info("Ending current turn!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    val output =
        advisorChat.sendMessage(
            "The turn is over. Please give a short summary of the agreed upon actions and decisions. Return the summary only in YAML format, no other text. "
                + "Optimize it for another AI to evaluate.");
    log.info("Chat response to JSON update: {}", output);
    turnCounter++;
    val nextTurnJson =
        chatClient
            .prompt()
            .options(
                OpenAiChatOptions.builder()
                    .responseFormat(ResponseFormat.builder().type(Type.JSON_OBJECT).build())
                    .build())
            .user(
                spec ->
                    spec.text(gameEnginePrompt)
                        .params(gameProperties.toMap())
                        .param("stateJSON", stateJson)
                        .param("decisionsYAML", DECISION_YAML))
            .call()
            .chatResponse();
    stateJson = nextTurnJson.getResult().getOutput().getText();
    log.info("updated JSON: \n{}", stateJson);
    return getStatusReport();
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
                + "Engage in a dialog with the player, possibly answering his questions, working on details of the proposals. "
                + "Only end the turn if the user actively expresses his desire to do so. Do so by calling the 'endTurn' tool. ");
    log.info("Chat response: {}", response);
    return GameStatusDataDto.builder()
        .stats(GameStatsDto.create(statusService.getGameData()))
        .chatMessage(response)
        .build();
  }
}
