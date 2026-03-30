package com.basilisk.core.autoconfigure;

import com.basilisk.core.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(GlobalExceptionHandler.class)
public class BasiliskCoreAutoConfiguration {
}
