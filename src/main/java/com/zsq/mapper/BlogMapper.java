package com.zsq.mapper;

import com.zsq.entity.Blog;
import com.zsq.util.PageQueryUtil;

import java.util.List;

public interface BlogMapper {
    int deleteByPrimaryKey(Long blogId);

    int insert(Blog record);

    int insertSelective(Blog record);

    Blog selectByPrimaryKey(Long blogId);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKeyWithBLOBs(Blog record);

    int updateByPrimaryKey(Blog record);

    List<Blog> findBlogList(PageQueryUtil pageUtil);

    int getTotalBlogs(PageQueryUtil pageUtil);

    int deleteBatch(Integer[] ids);

    List<Blog> findBlogListByType(int type, int i);

    List<Blog> getBlogsPageByTagId(PageQueryUtil pageUtil);

    int getTotalBlogsByTagId(PageQueryUtil pageUtil);
}