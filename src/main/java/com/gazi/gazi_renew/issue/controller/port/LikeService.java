package com.gazi.gazi_renew.issue.controller.port;

import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.domain.dto.LikeCreate;
import com.gazi.gazi_renew.issue.domain.dto.LikeDelete;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    Long likeIssue(LikeCreate likeCreate);
    void deleteLikeIssue(LikeDelete likeDelete);
}
