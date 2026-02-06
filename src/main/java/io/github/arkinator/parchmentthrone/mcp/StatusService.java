package io.github.arkinator.parchmentthrone.mcp;

import io.github.arkinator.parchmentthrone.mcp.data.GameDataDto;
import java.nio.charset.StandardCharsets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Data
@Service
@Slf4j
public class StatusService {

  private GameDataDto gameDataDto;
  private final VectorStore vectorStore;

  @Tool(description = "Provides a compact overview of the current state of the nation.")
  public String getNationStatus(@ToolParam(description = "Name of the nation") String nationName) {
    return vectorStore.similaritySearch("Get status for nation " + nationName).stream()
        .findFirst()
        .map(
            result -> {
              log.info(
                  "Found status for nation {} (score {}): \n{}",
                  nationName,
                  result.getScore(),
                  result.getText());
              return result.getText();
            })
        .orElse("No status found for nation " + nationName);
    //    log.info("Returning basic status for nation {}: \n{}", nationName, basicStatus);
    //    return basicStatus;
  }

  @Tool(description = "Updates the status for a nation.")
  public void updateNationStatus(
      @ToolParam(description = "Name of the nation") String nationName,
      @ToolParam(
              description =
                  "Basic status of the nation. Use the JSON format that has been provided")
          String nationStatus) {
    log.info("Updating basic status for nation {} to \n{}", nationName, nationStatus);
    vectorStore.add(
        new JsonReader(new ByteArrayResource(nationStatus.getBytes(StandardCharsets.UTF_8))).get());
    //    log.info("Updating basic status for nation {} to: \n{}", nationName, nationStatus);
    //    basicStatus = nationStatus;
  }

  @Tool(description = "Provides the current game data.")
  public GameDataDto getGameData() {
    log.info("Returning current game data: {}", gameDataDto);
    return gameDataDto;
  }

  @Tool(
      description =
          "Updates the game data, which includes nation status, money, political power, and mental energy.")
  public void updateGameData(GameDataDto gameData) {
    log.info("Updating game data to: {}", gameData);
    this.gameDataDto = gameData;
  }
}
