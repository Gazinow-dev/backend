package com.gazi.gazi_renew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GaziRenewApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaziRenewApplication.class, args);
    }

}
