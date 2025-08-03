package io.github.arkinator.parchmentthrone.mcp.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface to represent any type of political system.
 * This is the key to flexibility.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "systemType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = MonarchyDto.class, name = "monarchy"),
  @JsonSubTypes.Type(value = DemocracyDto.class, name = "democracy"),
  @JsonSubTypes.Type(value = OligarchyDto.class, name = "oligarchy")
})
public interface PoliticalSystemDto {}
