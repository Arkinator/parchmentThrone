package io.github.arkinator.parchmentthrone;

import io.github.arkinator.parchmentthrone.game.GameProperties;
import java.net.http.HttpClient;
import java.time.Duration;
import lombok.Data;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@Data
public class ParchmentThroneApplication {

  @Value("${spring.ai.openai.base-url}")
  private String openAiBaseUrl;

  @Value("${spring.ai.openai.chat.options.model}")
  private String model;

  public static void main(String[] args) {
    SpringApplication.run(ParchmentThroneApplication.class, args);
  }

  @Bean
  public EmbeddingModel embeddingModel() {
    return new OpenAiEmbeddingModel(
        OpenAiApi.builder().apiKey(new NoopApiKey()).baseUrl(openAiBaseUrl).build());
  }

  @Bean
  public ChatModel chatModel(@Autowired GameProperties gameProperties) {
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
    return OpenAiChatModel.builder().defaultOptions(openAiChatOptions).openAiApi(openAiApi).build();
  }
}
