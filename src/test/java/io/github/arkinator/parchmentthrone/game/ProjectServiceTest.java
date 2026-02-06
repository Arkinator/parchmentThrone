package io.github.arkinator.parchmentthrone.game;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.arkinator.parchmentthrone.game.dto.GameProjectEntity;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class ProjectServiceTest {
  @Autowired private ProjectService projectService;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void assertLoadedProjectsArePresent() {
    assertThat(projectService.getProject("YoungPlan"))
      .hasFieldOrPropertyWithValue("projectName", "YoungPlanAgreement");
  }

  @SneakyThrows
  @Test
  void checkGenerationAgent() {
    projectService.addProject("name: Currency Replacement\n"
        + "description: To combat state depth we invent a new currency that will replace the Reichsmark\n"
        + "status: PLANNED\n"
        + "createdAt: 2023-10-01T00:00:00Z\n"
        + "updatedAt: 2023-10-01T00:00:00Z\n");
    final GameProjectEntity queriedProject = projectService.getProject("Currency Replacement");
    System.out.println(objectMapper.writeValueAsString(queriedProject));
    assertThat(queriedProject)
      .hasFieldOrPropertyWithValue("projectName", "Currency Replacement Project");
    assertThat(queriedProject.getEstimatedCostGold()).isBetween(9.0E6, 1.1E8);
  }
}
