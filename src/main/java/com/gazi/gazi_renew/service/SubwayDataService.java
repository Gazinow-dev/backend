package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.domain.Subway;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import com.gazi.gazi_renew.repository.SubwayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubwayDataService {
    private final SubwayRepository subwayRepository;

    public void saveStationsFromJsonFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("station_coordinate.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Subway[] subways = objectMapper.readValue(resource.getFile(), Subway[].class);

        for (Subway subway : subways) {
            subwayRepository.save(subway);
        }
        log.info("지하철 정보 업로드 완료");
    }

    public SubwayDataResponse getCoordinateByNameAndLine(String subwayName, String line) {
        SubwayDataResponse subwayCoordinates = subwayRepository.findCoordinateByNameAndLine(subwayName, line);
        return subwayCoordinates;
    }
}
