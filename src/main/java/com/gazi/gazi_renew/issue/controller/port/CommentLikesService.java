package com.gazi.gazi_renew.issue.controller.port;

import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.dto.CommentLikesCreate;

public interface CommentLikesService {
    CommentLikes addLike(CommentLikesCreate commentLikesCreate);
    void removeLike(Long commentLikesId);

}
