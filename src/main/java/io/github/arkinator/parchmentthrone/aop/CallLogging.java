package io.github.arkinator.parchmentthrone.aop;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallLogging {
}