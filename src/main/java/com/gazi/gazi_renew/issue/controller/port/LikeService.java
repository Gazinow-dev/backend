package com.gazi.gazi_renew.issue.controller.port;

import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    Long likeIssue(Like like);
    void deleteLikeIssue(Like dto);

}
