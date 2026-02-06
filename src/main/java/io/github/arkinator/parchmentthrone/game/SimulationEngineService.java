package io.github.arkinator.parchmentthrone.game;

import io.github.arkinator.parchmentthrone.game.dto.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SimulationEngineService {
  private final GameStatus gameStatus;
  private final ProjectService projectService;

  public void runRound() {
    var newState = gameStatus.getCurrentState().toBuilder().build();
    for (val project : projectService.getAllProjectsForStatus(ProjectStatus.IN_PROGRESS)) {
      if (project.getImpacts() == null) continue;
      newState = project.applyTo(newState);
    }
    gameStatus.setCurrentState(newState);
  }
}
