package io.github.arkinator.parchmentthrone.game.characters;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.ai.document.Document;

@Data
public class GameCharacter {
  static final ObjectMapper objectMapper = new ObjectMapper();

  private String id;
  private String name;
  private String title;
  private String role;
  private List<String> factions;
  private List<String> ideology;
  private String ambition;
  private Agenda agenda;
  private Alignment alignment;
  private float trust;
  private List<String> methods;
  private List<String> access;
  private List<String> traits;
  private Memory mem;
  private String status;
  private Intrigue intrigue;

  public static GameCharacter fromDocument(Document doc) {
    try {
      return objectMapper.readValue(doc.getText(), GameCharacter.class);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to deserialize GameCharacter from document '" + doc.getText() + "'", e);
    }
  }

  public String toJson() {
    try {
      return objectMapper.writeValueAsString(this);
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize GameCharacter to JSON", e);
    }
  }

  @Data
  public static class Agenda {
    private String publicAgenda;
    private String privateAgenda;
  }

  @Data
  public class Memory {
    private LocalDate lastSeen;
    private List<String> rumors;
    private List<String> historyRefs;
  }

  @Data
  public class Intrigue {
    private float baseTendency; // 0-1 likelihood by personality
    private LocalDate lastIntrigue;
    private List<String> style; // e.g., "covert", "political_maneuver", "financial_sabotage"
    private List<String> triggers; // keywords or conditions that push them toward intrigue
  }

  @Data
  public class Alignment {
    private float player; // -1.0 = hostile, 1.0 = loyal
    private Map<String, Float> factions;
  }
}
