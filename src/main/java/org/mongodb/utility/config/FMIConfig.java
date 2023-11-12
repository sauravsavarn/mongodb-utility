package org.mongodb.utility.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mongodb.utility.loader.yaml.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

@Configuration //this marks the class as a source of bean definitions
@ConfigurationProperties(prefix = "fmi",  ignoreUnknownFields = false)
//this binds and validates the external configurations to a configuration class
@PropertySource(value = "classpath:config/fmi-config.yml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FMIConfig {

//    private Map<String, Map<String, List<Object>>> region;
private Map<String, Map<String, Map<String, String>>> region;
}
