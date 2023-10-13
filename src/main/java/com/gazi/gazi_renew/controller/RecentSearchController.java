package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.MemberRequest;
import com.gazi.gazi_renew.dto.RecentSearchRequest;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.RecentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/recentSearch")
@RestController
public class RecentSearchController {

    private final RecentSearchService recentSearchService;


    @PostMapping("/add")
    public ResponseEntity<Response.Body> signup(@RequestBody RecentSearchRequest dto) {
        return recentSearchService.recentAdd(dto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response.Body> delete(@RequestParam Long recentId){
        return recentSearchService.recentDelete(recentId);
    }
}
