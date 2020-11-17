package com.zsq.service.impl;

import com.zsq.entity.Comment;
import com.zsq.mapper.CommentMapper;
import com.zsq.service.CommentService;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public PageResult getCommentsPage(PageQueryUtil pageUtil) {
        List<Comment> comments = commentMapper.findBlogCommentList(pageUtil);
        int total = commentMapper.getTotalBlogComments(pageUtil);
        PageResult pageResult = new PageResult(comments, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    public Boolean checkDone(Integer[] ids) {
        return commentMapper.checkDone(ids) > 0;
    }

    @Override
    public Boolean reply(Long commentId, String replyBody) {
        Comment blogComment = commentMapper.selectByPrimaryKey(commentId);
        //blogComment不为空且状态为已审核，则继续后续操作
        if (blogComment != null && blogComment.getCommentStatus().intValue() == 1) {
            blogComment.setReplyBody(replyBody);
            blogComment.setReplyCreateTime(new Date());
            return commentMapper.updateByPrimaryKeySelective(blogComment) > 0;
        }
        return false;
    }

    //添加评论
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return commentMapper.deleteBatch(ids) > 0;
    }

    public Boolean addComment(Comment blogComment) {
        return commentMapper.insertSelective(blogComment) > 0;
    }
}
