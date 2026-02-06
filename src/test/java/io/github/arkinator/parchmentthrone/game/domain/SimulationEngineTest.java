package io.github.arkinator.parchmentthrone.game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.arkinator.parchmentthrone.game.GameStatus;
import io.github.arkinator.parchmentthrone.game.ProjectService;
import io.github.arkinator.parchmentthrone.game.SimulationEngineService;
import io.github.arkinator.parchmentthrone.game.dto.GameProjectEntity;
import io.github.arkinator.parchmentthrone.game.dto.ProjectStatus;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SimulationEngineTest {

  @Autowired private SimulationEngineService engine;
  @Autowired private GameStatus gameStatus;
  @Autowired private ProjectService projectService;

  @Test
  void testEffectApplication() {
    // Initial CountryState
    Demography demography =
        Demography.builder()
            .population(1000)
            .growthRate(0.01f)
            .ageStructure(Map.of())
            .urbanization(0.5f)
            .migrationBalance(0.0f)
            .build();
    Economy economy =
        Economy.builder()
            .gdp(1000f)
            .inequality(0.5f)
            .productivity(1.0f)
            .eliteSurplus(0.5f)
            .subsistenceSecurity(0.3f)
            .build();
    StateCapacity stateCapacity =
        StateCapacity.builder()
            .bureaucracy(0.5f)
            .military(0.5f)
            .taxation(0.5f)
            .lawEnforcement(0.5f)
            .corruption(0.1f)
            .build();
    Society society =
        Society.builder()
            .cohesion(0.5f)
            .unrest(0.2f)
            .trustInInstitutions(0.5f)
            .culturalDiversity(0.3f)
            .religionInfluence(0.2f)
            .build();
    Politics politics =
        Politics.builder()
            .regimeType(Politics.RegimeType.DEMOCRACY)
            .legitimacy(0.7f)
            .eliteCompetition(0.3f)
            .inclusivity(0.6f)
            .repression(0.1f)
            .build();
    Environment environment =
        Environment.builder()
            .arableLand(0.5f)
            .resourceBase(0.5f)
            .climateStress(0.2f)
            .pollution(0.1f)
            .build();

    CountryState state =
        CountryState.builder()
            .demography(demography)
            .economy(economy)
            .stateCapacity(stateCapacity)
            .society(society)
            .politics(politics)
            .environment(environment)
            .build();

    // Effect: Land Reform Act
    val project =
        GameProjectEntity.builder()
            .projectName("Land Reform Act")
            .impacts(
                CountryState.builder()
                    .economy(
                        Economy.builder().subsistenceSecurity(0.2f).eliteSurplus(-0.1f).build())
                    .build())
            .build();

    CountryState updated = project.applyTo(state);

    assertThat(updated.getEconomy().subsistenceSecurity()).isEqualTo(0.5f);
    assertThat(updated.getEconomy().eliteSurplus()).isEqualTo(0.4f);
  }

  @Test
  void testSimulationOneRound() {
    // Initial CountryState
    CountryState state =
        CountryState.builder()
            .demography(
                Demography.builder()
                    .population(1000)
                    .growthRate(0.01f)
                    .ageStructure(Map.of())
                    .urbanization(0.5f)
                    .migrationBalance(0.0f)
                    .build())
            .economy(
                Economy.builder()
                    .gdp(1000f)
                    .inequality(0.5f)
                    .productivity(1.0f)
                    .eliteSurplus(0.5f)
                    .subsistenceSecurity(0.3f)
                    .build())
            .stateCapacity(
                StateCapacity.builder()
                    .bureaucracy(0.5f)
                    .military(0.5f)
                    .taxation(0.5f)
                    .lawEnforcement(0.5f)
                    .corruption(0.1f)
                    .build())
            .society(
                Society.builder()
                    .cohesion(0.5f)
                    .unrest(0.2f)
                    .trustInInstitutions(0.5f)
                    .culturalDiversity(0.3f)
                    .religionInfluence(0.2f)
                    .build())
            .politics(
                Politics.builder()
                    .regimeType(Politics.RegimeType.DEMOCRACY)
                    .legitimacy(0.7f)
                    .eliteCompetition(0.3f)
                    .inclusivity(0.6f)
                    .repression(0.1f)
                    .build())
            .environment(
                Environment.builder()
                    .arableLand(0.5f)
                    .resourceBase(0.5f)
                    .climateStress(0.2f)
                    .pollution(0.1f)
                    .build())
            .build();

    // Memory
    CountryHistory memory =
        CountryHistory.builder()
            .recentHistory(new ArrayDeque<>())
            .narrativeSummary("")
            .snapshots(new ArrayList<>())
            .build();

    // SimulationEngine
    /* TODO        simulationEngineService
    .currentState(state)
    .memory(memory)
    .build();*/

    // Effect: Land Reform Act
    val project =
        GameProjectEntity.builder()
            .status(ProjectStatus.IN_PROGRESS)
            .projectName("Land Reform Act")
            .impacts(
                CountryState.builder()
                    .economy(
                        Economy.builder().subsistenceSecurity(0.2f).eliteSurplus(-0.1f).build())
                    .build())
            .build();
    projectService.addProject(project);

    //        CountryState oldState = engine.getCurrentState().snapshot();
    gameStatus.setCurrentState(state);
    engine.runRound();
    CountryState newState = gameStatus.getCurrentState();

    assertThat(newState.getEconomy().subsistenceSecurity()).isEqualTo(0.5f);
    assertThat(newState.getEconomy().eliteSurplus()).isEqualTo(0.4f);
    //      assertThat(engine.getMemory().recentHistory()).contains(oldState);
    //      assertThat(newState).isNotEqualTo(oldState);
  }
}
