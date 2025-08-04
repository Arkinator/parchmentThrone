package io.github.arkinator.parchmentthrone;

import lombok.Data;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.NoopApiKey;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
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
    return new OpenAiEmbeddingModel(OpenAiApi.builder().apiKey(new NoopApiKey()).baseUrl(openAiBaseUrl).build());
  }
}
