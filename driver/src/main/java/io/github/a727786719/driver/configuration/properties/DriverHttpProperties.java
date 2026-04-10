package io.github.a727786719.driver.configuration.properties;


import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Objects;

@ConfigurationProperties(prefix = "http")
public class DriverHttpProperties implements InitializingBean, EnvironmentAware {

    private Environment environment;

    private boolean enable = false;
    private String host = "0.0.0.0";
    private int port = 8080;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.enable = Objects.requireNonNullElse(this.environment.getProperty("HTTP.ENABLE", Boolean.class), this.enable);
        this.host = Objects.requireNonNullElse(this.environment.getProperty("HTTP.HOST", String.class), this.host);
        this.port = Objects.requireNonNullElse(this.environment.getProperty("HTTP.PORT", Integer.class), this.port);
    }
    
    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
