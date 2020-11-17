package com.zsq.service;

import com.zsq.controller.vo.BlogDetailVO;
import com.zsq.entity.Blog;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;

import java.util.List;

public interface BlogService {

    String saveBlog(Blog blog);

    Blog getBlogById(Long id);

    String updateBlog(Blog blog);

    PageResult getBlogsPage(PageQueryUtil pageUtil);

    boolean deleteBatch(Integer[] ids);

    List getBlogListForIndexPage(int i);

    PageResult getBlogsForIndexPage(int pageNum);

    PageResult getBlogsPageBySearch(String keyword, Integer page);

    PageResult getBlogsPageByCategory(String categoryName, Integer page);

    PageResult getBlogsPageByTag(String tagName, Integer page);

    BlogDetailVO getBlogDetail(Long blogId);
}
