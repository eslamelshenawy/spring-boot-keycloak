package com.keyclock.demo.config;

import com.keyclock.demo.service.KeycloakUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public KeycloakUtils keycloakUtils() {
        return new KeycloakUtils();
    }

    // Other bean definitions...

}