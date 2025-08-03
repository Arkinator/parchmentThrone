package io.github.arkinator.parchmentthrone.mcp;

import io.github.arkinator.parchmentthrone.mcp.data.*;
import io.github.arkinator.parchmentthrone.mcp.data.RecentEventsDto.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Data;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.bind.annotation.*;

@RestController
@Data
public class McpStatusController {

  private final McpStatusService mcpStatusService;

  @Tool(
    name = "getMilitaryStatus",
    description = "Provides information about armies, troop strength, morale, and tech level."
  )
  @Operation(summary = "Get military status", description = "Provides information about armies, troop strength, morale, and tech level.")
  @ApiResponse(responseCode = "200", description = "Military status retrieved")
  @GetMapping("/status/military")
  public MilitaryStatusDto getMilitaryStatus(@ToolParam(description = "Name of the nation") String nationName) {
    return mcpStatusService.getMilitaryStatus(nationName);
  }

  @Tool(
    name = "updateMilitaryStatus",
    description = "Updates the military status for a nation."
  )
  @Operation(summary = "Update military status", description = "Updates the military status for a nation.")
  @ApiResponse(responseCode = "200", description = "Military status updated")
  @PutMapping("/status/military")
  public MilitaryStatusDto updateMilitaryStatus(
    @ToolParam(description = "Name of the nation") String nationName,
    @RequestBody MilitaryStatusDto updateDto) {
    mcpStatusService.updateMilitaryStatus(nationName, updateDto);
    return mcpStatusService.getMilitaryStatus(nationName);
  }


  @Tool(
    name = "getDiplomaticRelations",
    description = "Provides the diplomatic status with other nations."
  )
  @Operation(summary = "Get diplomatic relations", description = "Provides the diplomatic status with other nations.")
  @ApiResponse(responseCode = "200", description = "Diplomatic relations retrieved")
  @GetMapping("/status/diplomacy")
  public DiplomaticRelationsDto getDiplomaticRelations(
    @ToolParam(description = "Name of the nation") String nationName) {
    return mcpStatusService.getDiplomaticRelations(nationName);
  }

  @Tool(
    name = "updateDiplomaticRelations",
    description = "Updates the diplomatic relations for a nation."
  )
  @Operation(summary = "Update diplomatic relations", description = "Updates the diplomatic relations for a nation.")
  @ApiResponse(responseCode = "200", description = "Diplomatic relations updated")
  @PutMapping("/status/diplomacy")
  public DiplomaticRelationsDto updateDiplomaticRelations(
    @ToolParam(description = "Name of the nation") String nationName,
    @RequestBody DiplomaticRelationsDto updateDto) {
    mcpStatusService.updateDiplomaticRelations(nationName, updateDto);
    return mcpStatusService.getDiplomaticRelations(nationName);
  }


  @Tool(
    name = "getInternalStability",
    description = "Analyzes unrest, government support, and rebellions."
  )
  @Operation(summary = "Get internal stability", description = "Analyzes unrest, government support, and rebellions.")
  @ApiResponse(responseCode = "200", description = "Internal stability retrieved")
  @GetMapping("/status/internal")
  public InternalStabilityDto getInternalStability(@ToolParam(description = "Name of the nation") String nationName) {
    return mcpStatusService.getInternalStability(nationName);
  }

  @Tool(
    name = "updateInternalStability",
    description = "Updates the internal stability for a nation."
  )
  @Operation(summary = "Update internal stability", description = "Updates the internal stability for a nation.")
  @ApiResponse(responseCode = "200", description = "Internal stability updated")
  @PutMapping("/status/internal")
  public InternalStabilityDto updateInternalStability(
    @ToolParam(description = "Name of the nation") String nationName,
    @RequestBody InternalStabilityDto updateDto) {
    mcpStatusService.updateInternalStability(nationName, updateDto);
    return mcpStatusService.getInternalStability(nationName);
  }

  @Tool(
    name = "getRecentEvents",
    description = "Provides a list of relevant events from recent rounds."
  )
  @Operation(summary = "Get recent events", description = "Provides a list of relevant events from recent rounds.")
  @ApiResponse(responseCode = "200", description = "Recent events retrieved")
  @GetMapping("/status/events")
  public RecentEventsDto getRecentEvents(@ToolParam(description = "Name of the nation") String nationName) {
    return mcpStatusService.getRecentEvents(nationName);
  }

  @Tool(
    name = "addRecentEvent",
    description = "Adds a new event to the list of recent events for a nation."
  )
  @Operation(summary = "Add recent event", description = "Adds a new event to the list of recent events for a nation.")
  @ApiResponse(responseCode = "200", description = "Recent event added")
  @PostMapping("/status/events")
  public RecentEventsDto addRecentEvent(
    @ToolParam(description = "Name of the nation") String nationName,
    @RequestBody EventDto eventDto) {
    mcpStatusService.addRecentEvent(nationName, eventDto);
    return mcpStatusService.getRecentEvents(nationName);
  }

  @Tool(
    name = "getPoliticalStatus",
    description = "Provides information about the political status of the nation."
  )
  @Operation(summary = "Get political status", description = "Provides information about the political status of the nation.")
  @ApiResponse(responseCode = "200", description = "Political status retrieved")
  @GetMapping("/status/political")
  public PoliticalStatusDto getPoliticalStatus(@ToolParam(description = "Name of the nation") String nationName) {
    return mcpStatusService.getPoliticalStatus(nationName);
  }

  @Tool(
    name = "updatePoliticalStatus",
    description = "Updates the political status for a nation."
  )
  @Operation(summary = "Update political status", description = "Updates the political status for a nation.")
  @ApiResponse(responseCode = "200", description = "Political status updated")
  @PutMapping("/status/political")
  public PoliticalStatusDto updatePoliticalStatus(
    @ToolParam(description = "Name of the nation") String nationName,
    @RequestBody PoliticalStatusDto updateDto) {
    mcpStatusService.updatePoliticalStatus(nationName, updateDto);
    return mcpStatusService.getPoliticalStatus(nationName);
  }
}