package com.zsq.service;

import com.zsq.entity.Comment;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;

public interface CommentService {

    public PageResult getCommentsPage(PageQueryUtil pageUtil);

    Boolean checkDone(Integer[] ids);

    Boolean reply(Long commentId, String replyBody);

    Boolean deleteBatch(Integer[] ids);

    Boolean addComment(Comment comment);
}
