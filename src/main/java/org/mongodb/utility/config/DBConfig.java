package org.mongodb.utility.config;

import lombok.*;
import org.mongodb.utility.loader.yaml.YamlPropertySourceFactory;
import org.mongodb.utility.domain.model.DatabaseModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.*;

//@PropertySource("classpath:config/mongodb-config.yml")
@Configuration //this marks the class as a source of bean definitions
//@EnableConfigurationProperties
//this annotation is used to enable @ConfigurationProperties annotated beans in the Spring application
@ConfigurationProperties(prefix = "database",  ignoreUnknownFields = false)
//this binds and validates the external configurations to a configuration class
@PropertySource(value = "classpath:config/mongodb-config.yml", factory = YamlPropertySourceFactory.class)
//@ToString
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DBConfig {

    private List<String> name;
    private Map<String, Map<String, Map<String, Map<String, DatabaseModel>>>> env;
}
