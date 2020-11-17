package com.zsq.mapper;

import com.zsq.entity.Comment;
import com.zsq.util.PageQueryUtil;

import java.util.List;

public interface CommentMapper {
    int deleteByPrimaryKey(Long commentId);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Long commentId);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

    List<Comment> findBlogCommentList(PageQueryUtil pageUtil);

    int getTotalBlogComments(PageQueryUtil pageUtil);

    int checkDone(Integer[] ids);

    int deleteBatch(Integer[] ids);
}