package io.github.arkinator.parchmentthrone.genesis;

import io.github.arkinator.parchmentthrone.mcp.StatusService;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.io.Resource;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.stringtemplate.v4.*;

@Value
@Slf4j
public class GenesisExecutor {

  private static OpenAiChatModel chatModel;
  int startYear;
  String playerNationName;
  StatusService mcpBasicStatus;
  Resource systemPromptResource;
  Resource structureNationResource;
  String openAiBaseUrl;
  String model;

  @SneakyThrows
  public void execute() {
    log.info("---- Weltgenerierung beginnt ----");

    log.info(
        "Verwende folgende Start-Parameter: Jahr={}, Spielernation={}",
        startYear,
        playerNationName);

    log.info("Initialisiere ChatClient");

    generateChatModel();

    prepareModelForBasicCreation();

    final String nationStructure =
        renderPrompt(structureNationResource.getContentAsString(StandardCharsets.UTF_8));
    executeGenerationStep(nationStructure);

    log.info("---- Weltgenerierung erfolgreich abgeschlossen ----");
  }

  @SneakyThrows
  private void prepareModelForBasicCreation() {
    final String systemPrompt =
        renderPrompt(systemPromptResource.getContentAsString(StandardCharsets.UTF_8));
    log.debug("System-Prompt vorbereitet: {}", systemPrompt);
    chatModel.call(Prompt.builder().messages(new SystemMessage(systemPrompt)).build());
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
    var openAiChatOptions = OpenAiChatOptions.builder().model(model).temperature(0.4).build();
    chatModel =
        OpenAiChatModel.builder().defaultOptions(openAiChatOptions).openAiApi(openAiApi).build();
  }

  private void executeGenerationStep(String prompt, Map.Entry<String, String>... additionalParams) {
    log.info("Sende Prompt an den World Genesis Agent... (additionalParams: {})", additionalParams);

    log.info("Warte auf die Antwort des World Genesis Agenten...");
    val response =
        chatModel.call(
            Prompt.builder()
                .messages(new UserMessage(renderPrompt(prompt, additionalParams)))
                .build());
    log.info("Antwort des World Genesis Agenten erhalten: \n\n{}\n\n", response);
    if (response == null) {
      log.error(
          "Keine Antwort vom World Genesis Agenten erhalten. Bitte überprüfe die Konfiguration.");
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
