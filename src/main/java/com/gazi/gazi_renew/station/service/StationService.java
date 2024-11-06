package com.gazi.gazi_renew.station.service;

import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StationService {

    private final SubwayRepository subwayRepository;
    public Station getCoordinateByNameAndLine(String subwayName, String line) {
        Station station = subwayRepository.findCoordinateByNameAndLine(subwayName, line);
        return station;
    }

    public List<Station> getSubwayInfo(String subwayName) {
        List<Station> stationList = subwayRepository.findByNameStartingWith(subwayName);
        return stationList;
    }

    @Transactional(readOnly = true)
    public Station getStationByNameAndLine(String name, String line){
        try{
            System.out.println(line);
            if(line.equals("수도권 9호선(급행)")){
                line = "수도권 9호선";
            }

            List<Station> stationList = subwayRepository.findByNameContainingAndLine(name,line);

            Station firstStation = stationList.get(0);
            int k = 0;
            // 필터링
            if(!stationList.isEmpty() && stationList.size() >=2){

                for(Station station : stationList){
                    int stationLength = station.getName().length(); // 찾은 entity 역글자수
                    int result = stationLength - name.length();

                    if(k > result){
                        firstStation = station;
                        k = result;
                    }
                }
            }
            return firstStation;
        }catch (EntityNotFoundException e){
            log.error(e.getMessage());
            return null;
        }catch (IndexOutOfBoundsException e){
            return null;
        }


    }
}
