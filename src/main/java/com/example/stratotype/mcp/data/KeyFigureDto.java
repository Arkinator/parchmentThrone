package com.example.stratotype.mcp.data;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class KeyFigureDto {
  @Schema(description = "Name of the key figure")
  private String name;

  @Schema(description = "Role of the key figure")
  private String role;

  @Schema(description = "Sphere of influence, e.g., 'Military', 'Economy', 'Religion'")
  private String influenceSphere;

  @Schema(description = "A brief description of their core motivations")
  private String motives;
}