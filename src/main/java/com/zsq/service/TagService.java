package com.zsq.service;

import com.zsq.entity.BlogTagCount;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;

import java.util.List;

public interface TagService {

    PageResult getTagPage(PageQueryUtil pageQueryUtil);

    boolean insertTag(String tagName);

    boolean delete(Integer[] ids);

    List<BlogTagCount> getBlogTagCountForIndex();
}
