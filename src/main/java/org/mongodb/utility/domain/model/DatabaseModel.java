package org.mongodb.utility.domain.model;

import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DatabaseModel {
    @Getter
    @Setter
    private String hostname;
    private String username;
    private String password;
    private Map<String, String> keystore;
    private Map<String, String> truststore;
}
