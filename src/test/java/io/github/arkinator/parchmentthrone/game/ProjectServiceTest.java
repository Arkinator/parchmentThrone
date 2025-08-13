package io.github.arkinator.parchmentthrone.game;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class ProjectServiceTest {
  @Autowired private ProjectService projectService;

  @Test
  void assertLoadedProjectsArePresent() {
    assertThat(projectService.getProject("YoungPlan"))
      .hasFieldOrPropertyWithValue("projectName", "YoungPlanAgreement");
  }

  @Test
  void checkGenerationAgent() {
    projectService.addProject("name: Currency Replacement\n"
        + "description: To combat state depth we invent a new currency that will replace the Reichsmark\n"
        + "status: PLANNED\n"
        + "createdAt: 2023-10-01T00:00:00Z\n"
        + "updatedAt: 2023-10-01T00:00:00Z\n");

  }
}
