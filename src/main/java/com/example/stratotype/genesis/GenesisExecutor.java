package com.example.stratotype.genesis;

import com.example.stratotype.mcp.McpBasicStatus;
import com.example.stratotype.mcp.McpStatusService;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.support.ToolCallbacks;
import org.stringtemplate.v4.*;
import org.springframework.core.io.Resource;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.util.Tuple;

@Value
@Slf4j
public class GenesisExecutor {

  private static OpenAiChatModel chatModel;
  int startYear;
  String playerNationName;
  McpBasicStatus mcpBasicStatus;
  Resource systemPromptResource;

  public void execute() {
    log.info("---- Weltgenerierung beginnt ----");

    log.info("Verwende folgende Start-Parameter: Jahr={}, Spielernation={}", startYear, playerNationName);

    log.info("Initialisiere ChatClient");

    generateChatModel();

    prepareModelForBasicCreation();

    executeGenerationStep("Lets begin by updating the status for <playerNationName> in the year <startYear>.");
    List<String> otherNations = extractListFromResponse(
      "Please provide a list of other important nations in the world besides <playerNationName> in the year <startYear>."
      + "This includes major powers, regional leaders, and any nations that have a significant impact on global politics. "
      + "Include all neighboring nations and those with historical ties to <playerNationName>."
      + "Ensure that the status reflects their current situation in the year <startYear>. Update the status individually for each nation, "
      + "as many times as necessary, until all relevant nations are covered. This should be anything from 10 to 20 nations."
      + "The list should be in the format: [\"Nation1\", \"Nation2\", \"Nation3\"]."
      + " Please provide the list in a single response, formatted as JSON.");
    otherNations.forEach(n -> executeGenerationStep("Now update the status for the nation <otherNationName>. "
                                                    + "Please ensure that the status reflects their current situation in the year <startYear>.",
      Pair.of("otherNationName", n)));

    log.info("---- Weltgenerierung erfolgreich abgeschlossen ----");
  }

  private List<String> extractListFromResponse(String s) {
    log.info("Sende Prompt an den World Genesis Agenten, um eine Liste zu erhalten...");

    val response = chatModel.call(s);

    // Assuming the response is a JSON array of strings
    log.info("Antwort des World Genesis Agenten erhalten: {}", response);
    final List<String> result = List.of(response.replaceAll("[\\[\\]\"]", "").split(","));
    log.info("Extrahierte Liste: {}", result);
    return result;
  }

  @SneakyThrows
  private void prepareModelForBasicCreation() {

    final String systemPrompt = renderPrompt(systemPromptResource.getContentAsString(StandardCharsets.UTF_8));
    log.debug("System-Prompt vorbereitet: {}", systemPrompt);
    chatModel.call(Prompt.builder()
      .messages(new SystemMessage(systemPrompt))
      .build());
  }

  private static void generateChatModel() {
    OpenAiApi openAiApi = OpenAiApi.builder()
      .baseUrl("http://127.0.0.1:1235")
      .apiKey(new NoopApiKey())
      .webClientBuilder(WebClient.builder()
        .clientConnector(new JdkClientHttpConnector(HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_1_1)
          .connectTimeout(Duration.ofSeconds(30))
          .build())))
      .restClientBuilder(RestClient.builder()
        .requestFactory(new JdkClientHttpRequestFactory(HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_1_1)
          .connectTimeout(Duration.ofSeconds(30))
          .build())))
      .build();
    var openAiChatOptions = OpenAiChatOptions.builder()
      .model("llmbg-tooluse-27b-v1.0-i1")
//      .model("llama-3-groq-8b-tool-use")
      .temperature(0.4)
      .build();
    chatModel = OpenAiChatModel.builder()
      .defaultOptions(openAiChatOptions)
      .openAiApi(openAiApi).build();
  }

  private void executeGenerationStep(String prompt, Map.Entry<String, String>... additionalParams) {
    log.info("Sende Prompt an den World Genesis Agent... (additionalParams: {})", additionalParams);

    log.info("Warte auf die Antwort des World Genesis Agenten...");
    val response = chatModel.call(Prompt.builder()
      .chatOptions(ToolCallingChatOptions.builder()
        .toolCallbacks(ToolCallbacks.from(mcpBasicStatus)).build())
      .messages(new UserMessage(renderPrompt(prompt, additionalParams))).build());
    log.info("Antwort des World Genesis Agenten erhalten: {}", response);
    if (response == null) {
      log.error("Keine Antwort vom World Genesis Agenten erhalten. Bitte überprüfe die Konfiguration.");
    }
  }

  private String renderPrompt(String prompt, Entry<String, String>... additionalParams) {
    final ST st = new ST(prompt);
    st.add("playerNationName", playerNationName);
    st.add("startYear", startYear);
    for (var param : additionalParams) {
      st.add(param.getKey(), param.getValue());
    }
    return st.render();
  }
}
