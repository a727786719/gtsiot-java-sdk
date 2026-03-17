package io.github.a727786719.logger;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(GraalvmRuntimeHits.class)
public class AiriotLoggerAutoConfiguration {

}
