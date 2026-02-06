package io.github.arkinator.parchmentthrone.game;

import static java.util.Map.*;
import static org.springframework.ai.vectorstore.SimpleVectorStore.EmbeddingMath.cosineSimilarity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arkinator.parchmentthrone.game.dto.GameProjectEntity;
import io.github.arkinator.parchmentthrone.game.dto.ProjectStatus;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

  private final HashMap<String, GameProjectEntity> gameProjectMap = new HashMap<>();
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ChatClient chatClient;
  @Autowired private EmbeddingModel embeddingModel;
  @Autowired private GameStatus gameStatus;

  @Value("classpath:/prompts/project-generator.st")
  private Resource projectGeneratorPrompt;

  @SneakyThrows
  @EventListener(ApplicationReadyEvent.class)
  private void onApplicationReadyEvent() {
    gameProjectMap.putAll(
        objectMapper.readValue(
            getClass().getResourceAsStream("/game/projects.json"),
            objectMapper
                .getTypeFactory()
                .constructMapType(HashMap.class, String.class, GameProjectEntity.class)));
  }

  @SneakyThrows
  @Tool
  public void addProject(
      @ToolParam(description = "description of the project as an AI ready YAML")
      String projectDescription) {
    val projectEntity =
        chatClient
            .prompt()
            .user(
                resolveTemplate(
                    projectGeneratorPrompt,
                    Map.of(
                        "stateJson",
                        gameStatus.getStateJson(),
                        "yamlProposal",
                        projectDescription)))
            .call()
            .entity(GameProjectEntity.class);
    log.info("Adding project with description: {}", projectDescription);
    addProject(projectEntity);
  }

  @SneakyThrows
  private String resolveTemplate(Resource resource, Map<String, String> templateMap) {
    String content = resource.getContentAsString(StandardCharsets.UTF_8);
    for (Map.Entry<String, String> entry : templateMap.entrySet()) {
      content = content.replace("{" + entry.getKey() + "}", entry.getValue());
    }
    return content;
  }

  public void addProject(GameProjectEntity project) {
    log.info("Adding project: {}", project);
    gameProjectMap.put(project.getProjectName(), project);
  }

  @Tool
  public GameProjectEntity getProject(String projectName) {
    log.info("Retrieving project: {}", projectName);
    float[] queryEmbedding = embeddingModel.embed(projectName);
    return gameProjectMap.values().stream()
        .map(project -> calculateProjectSimilarity(project, queryEmbedding))
        .filter(entry -> entry.getKey() >= 0.8)
        .sorted((e1, e2) -> Double.compare(e2.getKey(), e1.getKey()))
        .limit(1)
        .map(Entry::getValue)
        .findFirst()
        .orElse(null);
  }

  private SimpleEntry<Double, GameProjectEntity> calculateProjectSimilarity(
      GameProjectEntity project, float[] queryEmbedding) {
    try {
      if (project.getProjectName() == null || project.getProjectName().isEmpty()) {
        return new SimpleEntry<>(0.0, project);
      }
      val projectEmbedding = embeddingModel.embed(project.getProjectName());
      double similarity = cosineSimilarity(queryEmbedding, projectEmbedding);
      return new SimpleEntry<>(similarity, project);
    } catch (RuntimeException e) {
      log.error("Error calculating similarity for project: {}", project, e);
      return new SimpleEntry<>(0.0, project);
    }
  }

  @Tool
  public List<GameProjectEntity> getAllProjectsForStatus(ProjectStatus projectStatus) {
    val result =
        gameProjectMap.values().stream()
            .filter(project -> project.getStatus() == projectStatus)
            .toList();
    log.info("Retrieving all projects with status {}: {}", projectStatus, result.size());
    return result;
  }
}
