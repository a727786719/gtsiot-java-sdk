package io.github.a727786719.driver.configuration.properties;


import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Objects;

@ConfigurationProperties(prefix = "datafile")
public class DriverDataFileProperties implements InitializingBean, EnvironmentAware {

    private Environment environment;

    private boolean enable = false;
    private String path = "./data.json";

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.enable = Objects.requireNonNullElse(this.environment.getProperty("DATAFILE.ENABLE", Boolean.class), this.enable);
        this.path = Objects.requireNonNullElse(this.environment.getProperty("DATAFILE.PATH", String.class), this.path);
    }
    
    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
