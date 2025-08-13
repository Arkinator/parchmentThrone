package io.github.arkinator.parchmentthrone.game.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProjectStatus {
  PROPOSED,
  IN_PROGRESS,
  COMPLETED,
  ON_HOLD,
  CANCELLED;

  public static ProjectStatus fromString(String value) {
    return switch (value.toUpperCase()) {
      case "PROPOSED", "SUGGESTED" -> PROPOSED;
      case "IN_PROGRESS", "STARTED", "ONGOING" -> IN_PROGRESS;
      case "COMPLETED", "FINISHED" -> COMPLETED;
      case "ON_HOLD", "PAUSED" -> ON_HOLD;
      case "CANCELLED", "ABORTED" -> CANCELLED;
      default -> throw new IllegalArgumentException("Unknown value: " + value);
    };
  }

  @JsonCreator
  public static ProjectStatus forValue(String value) {
    return fromString(value);
  }
}
