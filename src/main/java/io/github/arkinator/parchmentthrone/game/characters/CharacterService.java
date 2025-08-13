package io.github.arkinator.parchmentthrone.game.characters;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStore;
import org.springframework.stereotype.Service;

/**
 *TODO: curently out-of-order, schema needed for character generation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CharacterService {
  private final EmbeddingModel embeddingModel;
  private final WeaviateVectorStore vectorStore;
  private final ChatClient chatClient;
  private final String characterSchema =
"""
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "GameCharacter",
  "type": "object",
  "properties": {
    "id": { "type": "string" },
    "name": { "type": "string" },
    "title": { "type": "string" },
    "role": { "type": "string" },
    "factions": {
      "type": "array",
      "items": { "type": "string" }
    },
    "ideology": {
      "type": "array",
      "items": { "type": "string" }
    },
    "ambition": { "type": "string" },
    "agenda": {
      "$ref": "#/definitions/Agenda"
    },
    "alignment": {
      "$ref": "#/definitions/Alignment"
    },
    "trust": { "type": "number" },
    "methods": {
      "type": "array",
      "items": { "type": "string" }
    },
    "access": {
      "type": "array",
      "items": { "type": "string" }
    },
    "traits": {
      "type": "array",
      "items": { "type": "string" }
    },
    "mem": {
      "$ref": "#/definitions/Memory"
    },
    "status": { "type": "string" },
    "intrigue": {
      "$ref": "#/definitions/Intrigue"
    }
  },
  "required": ["id", "name"],
  "definitions": {
    "Agenda": {
      "type": "object",
      "properties": {
        "publicAgenda": { "type": "string" },
        "privateAgenda": { "type": "string" }
      }
    },
    "Memory": {
      "type": "object",
      "properties": {
        "lastSeen": { "type": "string", "format": "date" },
        "rumors": {
          "type": "array",
          "items": { "type": "string" }
        },
        "historyRefs": {
          "type": "array",
          "items": { "type": "string" }
        }
      }
    },
    "Intrigue": {
      "type": "object",
      "properties": {
        "baseTendency": { "type": "number" },
        "lastIntrigue": { "type": "string", "format": "date" },
        "style": {
          "type": "array",
          "items": { "type": "string" }
        },
        "triggers": {
          "type": "array",
          "items": { "type": "string" }
        }
      }
    },
    "Alignment": {
      "type": "object",
      "properties": {
        "player": { "type": "number" },
        "factions": {
          "type": "object",
          "additionalProperties": { "type": "number" }
        }
      }
    }
  }
}
""";

  @Tool(description = "Generate a new character with specified attributes")
  public void generateCharacter(String name, String description, @ToolParam(description = "about 10 of: factions, parties, attitudes") List<String> tags) {
    log.info("Generating character with name {}, description {}", name, description);
    String prompt =
        String.format(
            "Generate a historically accurate character for a medieval setting. "
                + "Name: %s. Description: %s. Factions/Tags: %s. "
                + "Return only the character's name, description, and factions/tags in JSON format."
                + "response_format: {type: \"json_schema\", schema: \""+characterSchema+"\"}",
            name, description, String.join(", ", tags));
    val character = chatClient.prompt(prompt).call().entity(GameCharacter.class);
    embeddingModel.embed(name);
    vectorStore.add(
        List.of(
            Document.builder()
                .text(character.toJson())
                .id(name)
                .metadata(Map.of("tags", String.join(",", tags)))
                .build()));
    log.info("Generated character {}: \n{}", name, character.toJson());
//    return character;
  }

  @Tool(description = "Retrieve a character by their name")
  public GameCharacter getCharacterByName(String name) {
    log.info("Retrieving character by name: {}", name);
    return vectorStore.similaritySearch(name).stream()
      .peek(character -> log.info("Found character with score {}", character.getScore()))
//        .filter(doc -> doc.getMetadata().get("name").equals(name))
      .sorted(Comparator.comparing(Document::getScore))
        .findFirst()
        .map(GameCharacter::fromDocument)
        .orElse(null);
  }

  @Tool(description = "Retrieve characters by factions they belong to")
  public List<GameCharacter> getCharactersByTags(List<String> tags) {
    final List<GameCharacter> result = vectorStore.similaritySearch(String.join(" ", tags)).stream()
      .map(GameCharacter::fromDocument)
      .filter(
        character ->
          character.getFactions() != null
          && tags.stream().anyMatch(tag -> character.getFactions().contains(tag)))
      .toList();
    log.info("Retrieving characters by tags: {}, returning {}", String.join(", ", tags), result.stream().map(GameCharacter::getName).toList());
    return result;
  }
}
