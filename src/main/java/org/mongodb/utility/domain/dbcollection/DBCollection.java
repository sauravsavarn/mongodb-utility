package org.mongodb.utility.domain.dbcollection;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
public class DBCollection {

    public List<String> dbcollections;

    public List<String> getDbcollections() {
        return dbcollections;
    }

    public void setDbcollections(List<String> dbcollections) {
        this.dbcollections = dbcollections;
    }
}
