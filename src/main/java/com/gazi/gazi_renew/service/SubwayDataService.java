package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.domain.Subway;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import com.gazi.gazi_renew.repository.SubwayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubwayDataService {

    private final Response response;
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

    public ResponseEntity<Response.Body> getSubwayInfo(String subwayName){
        List<Subway> subways = subwayRepository.findByNameStartingWith(subwayName);
        List<SubwayDataResponse.SubwayInfo> subwayInfos = subways.stream()
                .map(
                        subway ->{
                            SubwayDataResponse.SubwayInfo subwayInfo = SubwayDataResponse.SubwayInfo.builder()
                                    .name(subway.getName())
                                    .line(subway.getLine())
                                    .build();
                            return subwayInfo;
                        }
                ).collect(Collectors.toList());

        return response.success(subwayInfos,"지하철 검색 성공", HttpStatus.OK);
    }
}
