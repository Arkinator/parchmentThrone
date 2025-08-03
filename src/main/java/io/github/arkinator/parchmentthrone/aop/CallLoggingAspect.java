package io.github.arkinator.parchmentthrone.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CallLoggingAspect {

  private static final ObjectWriter PRETTY_PRINTER = new ObjectMapper()
    .writerWithDefaultPrettyPrinter();
  private static final Logger logger = LoggerFactory.getLogger(CallLoggingAspect.class);

  @Pointcut("@within(io.github.arkinator.parchmentthrone.aop.CallLogging)")
  public void beanAnnotatedWithCallLogging() {
  }

  @Before("beanAnnotatedWithCallLogging() && execution(* *(..))")
  @SneakyThrows
  public void logMethodEntry(JoinPoint joinPoint) {
    logger.info("Entering: {}.{} with args: {}",
      joinPoint.getSignature().getDeclaringTypeName(),
      joinPoint.getSignature().getName(),
      PRETTY_PRINTER.writeValueAsString(joinPoint.getArgs()));
  }

  @AfterReturning(pointcut = "beanAnnotatedWithCallLogging() && execution(* *(..))", returning = "result")
  public void logMethodExit(JoinPoint joinPoint, Object result) {
    logger.trace("Exiting: {}.{} - return: {}",
      joinPoint.getSignature().getDeclaringTypeName(),
      joinPoint.getSignature().getName(),
      result);
  }
}
