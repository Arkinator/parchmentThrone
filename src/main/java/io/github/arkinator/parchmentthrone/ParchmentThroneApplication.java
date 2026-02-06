package io.github.arkinator.parchmentthrone;

import io.github.arkinator.parchmentthrone.game.GameProperties;
import io.github.arkinator.parchmentthrone.game.domain.*;
import io.github.arkinator.parchmentthrone.game.domain.Politics.RegimeType;
import java.util.Map;
import lombok.Data;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
  public ChatClient chatClient(
      @Autowired GameProperties gameProperties, @Autowired ChatClient.Builder builder) {
    /*    OpenAiApi openAiApi =
        OpenAiApi.builder()
            .baseUrl(openAiBaseUrl)
            .apiKey("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")
           /* .webClientBuilder(
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
                                .build())))*
            .build();
    var openAiChatOptions =
        OpenAiChatOptions.builder()
            .model(gameProperties.getGameEngineModel())
            .temperature(0.4)
            .build();
    return OpenAiChatModel.builder().defaultOptions(openAiChatOptions).openAiApi(openAiApi).build();*/
    return builder.build();
  }
}
