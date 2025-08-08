package io.github.arkinator.parchmentthrone.game;

import io.github.arkinator.parchmentthrone.game.dto.GameStatsDto;
import io.github.arkinator.parchmentthrone.mcp.StatusService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.io.Resource;
import org.stringtemplate.v4.ST;

@Value
@RequiredArgsConstructor
@Slf4j
public class GameChat {

  ChatClient chatClient;
  List<Message> history = new ArrayList<>();
  GameProperties gameProperties;
  StatusService statusService;
  ToolCallingChatOptions chatOptions;
  Resource initialPrompt;
  @Getter Map<String, String> placeholders;

  private String renderPrompt(String rawPrompt, Map.Entry<String, String>... additionalEntries) {
    final ST st = new ST(rawPrompt);
    // add all game properties to the template using reflection
    Stream.of(gameProperties.getClass().getDeclaredFields())
        .forEach(
            field -> {
              field.setAccessible(true);
              try {
                st.add(field.getName(), field.get(gameProperties));
              } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
              }
            });
//    st.add("stateJSON", statusService.getNationStatus("germany"));
    // add additional entries to the template
    Stream.of(additionalEntries).forEach(entry -> st.add(entry.getKey(), entry.getValue()));

    return st.render();
  }

  public String sendMessage(String message, Map.Entry<String, String>... additionalEntries) {
    final String prompt = renderPrompt(message, additionalEntries);
    history.add(new UserMessage(prompt));
    val reply =
        chatClient
            .prompt(Prompt.builder().messages(history).chatOptions(chatOptions).build())
            .call()
            .chatResponse()
            .getResult()
            .getOutput();
    log.info("Reply to message: {}", reply.getText());
    history.add(reply);
    return reply.getText();
  }

  @SneakyThrows
  public String initialize() {
    history.clear();
    String promptTemplate =
        new String(initialPrompt.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

    String resolvedPrompt = promptTemplate;
    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
      resolvedPrompt = resolvedPrompt.replace("{" + entry.getKey() + "}", entry.getValue());
    }

    history.add(new UserMessage(resolvedPrompt));
    val reply =
        chatClient
            .prompt(Prompt.builder().messages(history).chatOptions(chatOptions).build())
            .call()
            .chatResponse()
            .getResult()
            .getOutput();
    log.info("Reply to init step: {}", reply.getText());
    history.add(reply);
    return reply.getText();
  }
}
