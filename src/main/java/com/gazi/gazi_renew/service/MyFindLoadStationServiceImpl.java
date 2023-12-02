package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyFindLoadStationServiceImpl implements MyFindLoadStationService{
    private final Response response;

    @Override
    public ResponseEntity<Response.Body> addStation() {
        return null;
    }
}
