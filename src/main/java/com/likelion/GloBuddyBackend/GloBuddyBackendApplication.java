package com.likelion.GloBuddyBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class GloBuddyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GloBuddyBackendApplication.class, args);
    }

}
