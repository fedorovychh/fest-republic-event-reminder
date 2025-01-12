package io.service.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    GoogleAuth googleAuth() {
        return new GoogleAuth();
    }
}
