package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface FindRoadService {

    ResponseEntity<Response.Body> findRoad(Double sx, Double sy, Double ex, Double ey) throws IOException;
    ResponseEntity<Response.Body> subwayRouteSearch(Long  CID, Long SID, Long EID, int sopt) throws IOException;

}
