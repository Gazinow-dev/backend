package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.LikeRequest;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    ResponseEntity<Response.Body> likeIssue(LikeRequest dto);
    ResponseEntity<Response.Body> deleteLikeIssue(LikeRequest dto);

}
