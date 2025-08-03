package com.example.stratotype.mcp.data;

import java.util.List;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for an Oligarchy, Theocracy, or other non-monarch/democracy.")
@Data
public class OligarchyDto implements PoliticalSystemDto {

  @Schema(description = "Name of the ruling body, e.g., 'The Merchant Guild Council'")
  private String rulingBodyName;

  @Schema(description = "Source of power, e.g., 'Wealth', 'Military Might', 'Divine Mandate'")
  private String powerSource;

  @Schema(description = "The members of the ruling body.")
  private List<KeyFigureDto> keyFigures;
}