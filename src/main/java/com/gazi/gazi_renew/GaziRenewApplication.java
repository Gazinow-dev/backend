package com.gazi.gazi_renew;

import com.gazi.gazi_renew.service.SubwayDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class GaziRenewApplication implements CommandLineRunner {
    private final SubwayDataService subwayDataService;

    public static void main(String[] args) {
        SpringApplication.run(GaziRenewApplication.class, args);
    }

    @Override
    public void run(String... args)throws Exception{
//        subwayDataService.saveStationsFromJsonFile();
    }
}
