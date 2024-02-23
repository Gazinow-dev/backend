package com.gazi.gazi_renew;

import com.gazi.gazi_renew.service.SubwayDataService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class GaziRenewApplication implements CommandLineRunner {
    private final SubwayDataService subwayDataService;

    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(GaziRenewApplication.class, args);
    }

    @Override
    public void run(String... args)throws Exception{
//        subwayDataService.saveStationsFromJsonFile();
    }
}
