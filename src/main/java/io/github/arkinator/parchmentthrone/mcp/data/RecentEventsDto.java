package io.github.arkinator.parchmentthrone.mcp.data;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RecentEventsDto {
  private String nationName;
  private List<EventDto> events;

  @Data
  public static class EventDto {
    private int turnNumber;
    private String eventType; // e.g., "Famine", "War Declared"
    private String description;
    private String source;
    private Map<String, Object> impactDetails; // e.g., {"population_change": -10000}
  }
}