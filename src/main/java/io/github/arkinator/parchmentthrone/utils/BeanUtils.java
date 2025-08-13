package io.github.arkinator.parchmentthrone.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class BeanUtils {
  private static final ObjectMapper OBJECT_MAPPER =
    new ObjectMapper().setSerializationInclusion(Include.NON_NULL);

  public static Map<String, Object> toMap(Object target) {
    return OBJECT_MAPPER.convertValue(target, new TypeReference<>() {});
  }
}
