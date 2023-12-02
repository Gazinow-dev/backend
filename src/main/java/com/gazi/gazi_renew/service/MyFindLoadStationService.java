package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

public interface MyFindLoadStationService {
    ResponseEntity<Response.Body> addStation();
}
