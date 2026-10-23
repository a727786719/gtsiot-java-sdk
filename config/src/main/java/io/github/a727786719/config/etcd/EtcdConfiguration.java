package io.github.a727786719.config.etcd;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({EtcdConfigProperties.class})
public class EtcdConfiguration {
}
