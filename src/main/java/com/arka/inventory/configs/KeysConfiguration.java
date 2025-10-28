package com.arka.inventory.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keys")
@Getter
@Setter
public class KeysConfiguration {
    String productBaseUrl;
    int apiTimeout;

}
