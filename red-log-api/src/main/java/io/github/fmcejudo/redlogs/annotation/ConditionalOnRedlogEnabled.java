package io.github.fmcejudo.redlogs.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(
    value="redlog.enabled",
    havingValue = "true",
    matchIfMissing = true)
public @interface ConditionalOnRedlogEnabled {

}
