package io.github.arkinator.parchmentthrone.game;

import static java.util.Map.*;
import static org.springframework.ai.vectorstore.SimpleVectorStore.EmbeddingMath.cosineSimilarity;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arkinator.parchmentthrone.game.dto.GameProjectEntity;
import io.github.arkinator.parchmentthrone.game.dto.ProjectStatus;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SimpleVectorStore.EmbeddingMath;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

  private HashMap<String, GameProjectEntity> gameProjectMap = new HashMap<>();
  private final ObjectMapper objectMapper;
  private final ChatModel chatModel;
  private final EmbeddingModel embeddingModel;
  private final String projectGenerationPrompt = """
SYSTEM ROLE:
Du bist der "Projekt-Generator" in einer politischen Grand-Strategy-Simulation.\s
Deine Aufgabe ist es, aus von anderen Bots gelieferten, lose formatierten YAML-Vorschlägen\s
ein sauberes, einheitliches Projekt-Objekt zu generieren.\s
Du bist Schiedsrichter: Du entscheidest realistisch über die tatsächlichen Effekte,\s
Kosten, Dauer und den Fortschritt, ohne parteiisch zu sein.

KONTEXT:
- Die Projekte werden in jeder Spielrunde neu erzeugt.
- Sie können politisch, wirtschaftlich, militärisch, sozial oder kulturell sein.
- Projekte haben Effekte auf den Spielzustand (stateJSON), aber auch indirekte Nebenwirkungen.
- Deine Aufgabe ist es, das Balancing glaubwürdig zu halten.
- Effekte müssen im Spielmechanismus umsetzbar sein (Zahlen, Wahrscheinlichkeiten, Zustandsänderungen).

EINGABE:
- Eine lose formatierte YAML-Struktur, die von einem anderen Bot kommt.\s
  Diese kann unvollständig, mehrdeutig oder unsauber sein.
- Mögliche Felder: Titel, Idee, Ziel, Ressourcen, Risiken, erwartete Effekte.

AUSGABE:
- Ein sauberes JSON-Objekt mit folgendem Schema:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "GameProjectEntity",
  "type": "object",
  "properties": {
    "projectName": { "type": "string" },
    "description": { "type": "string" },
    "estimatedCostGold": { "type": "number" },
    "estimatedCostPoliticalPower": { "type": "integer" },
    "status": { "type": "string" },
    "startDate": { "type": "string" },
    "endDate": { "type": "string" },
    "completionPercentage": { "type": "number" }
  },
  "required": [
    "projectName",
    "description",
    "estimatedCostGold",
    "estimatedCostPoliticalPower",
    "status"
  ]
}
```

RICHTLINIEN:
1. Prüfe die YAML auf fehlende Felder und ergänze diese plausibel.
2. Passe Effekte, Kosten und Dauer so an, dass sie in der Spielwelt sinnvoll und ausgewogen sind.
3. Falls ein Vorschlag übertrieben oder unrealistisch ist, skaliere ihn auf ein glaubwürdiges Maß.
4. Berücksichtige indirekte Effekte (z. B. politischer Widerstand, öffentliche Meinung).
5. Achte auf knappe, klare Titel und Beschreibungen.
6. Tags sollen für spätere KI-Suche relevant sein (z. B. Themen, beteiligte Gruppen, Epoche).

DEIN ZIEL:
Ein konsistentes, balanciertes Projektobjekt, das sofort ins Spiel eingefügt werden kann.
""";

  @SneakyThrows
  @EventListener(ApplicationReadyEvent.class)
  private void onApplicationReadyEvent() {
    gameProjectMap =
        objectMapper.readValue(
            getClass().getResourceAsStream("/game/projects.json"),
            objectMapper
                .getTypeFactory()
                .constructMapType(HashMap.class, String.class, GameProjectEntity.class));
  }

  @SneakyThrows
  @Tool
  public void addProject(
      @ToolParam(description = "description of the project as an AI ready YAML")
          String projectDescription) {
    log.info("Adding project with description: {}", projectDescription);
    GameProjectEntity project = objectMapper.readValue(projectDescription, GameProjectEntity.class);
    addProject(project);
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
        .map(
            project -> {
              val projectEmbedding = embeddingModel.embed(project.getProjectName());
              double similarity = cosineSimilarity(queryEmbedding, projectEmbedding);
              return new java.util.AbstractMap.SimpleEntry<>(similarity, project);
            })
        .filter(entry -> entry.getKey() >= 0.8)
        .sorted((e1, e2) -> Double.compare(e2.getKey(), e1.getKey()))
        .limit(1)
        .map(Entry::getValue)
        .findFirst()
        .orElse(null);
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
