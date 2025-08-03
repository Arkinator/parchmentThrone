package io.github.arkinator.parchmentthrone.mcp;

import io.github.arkinator.parchmentthrone.mcp.data.BasicStatusDto;
import lombok.Data;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Data
@Service
public class McpBasicStatus {

  private final McpStatusService mcpStatusService;

  @Tool(
    name = "getBasicStatus",
    description = "Provides a compact overview of the current state of the nation."
  )
  public BasicStatusDto getBasicStatus(@ToolParam(description = "Name of the nation") String nationName) {
    return mcpStatusService.getBasicStatus(nationName);
  }

  @Tool(
    name = "updateBasicStatus",
    description = "Updates the basic status for a nation."
  )
  BasicStatusDto updateBasicStatus(
    @ToolParam(description = "Name of the nation") String nationName,
    @ToolParam(description = "Basic status of the nation") BasicStatusDto updateDto) {
    mcpStatusService.updateBasicStatus(nationName, updateDto);
    return mcpStatusService.getBasicStatus(nationName);
  }
}
