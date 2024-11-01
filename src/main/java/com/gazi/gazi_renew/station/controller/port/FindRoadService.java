package com.gazi.gazi_renew.station.controller.port;

import com.gazi.gazi_renew.station.domain.FindRoadRequest;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface FindRoadService {

    ResponseEntity<Response.Body> subwayRouteSearch(Long  CID, Long SID, Long EID, int sopt) throws IOException;

    ResponseEntity<Response.Body> findRoad(FindRoadRequest request) throws IOException;
}
