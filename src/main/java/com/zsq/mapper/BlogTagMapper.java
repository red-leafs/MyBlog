package com.zsq.mapper;

import com.zsq.entity.BlogTagCount;

import java.util.List;

public interface BlogTagMapper {

    //获取标签数量
    public List<BlogTagCount> getBlogTagCountForIndex();

    List<BlogTagCount> getBlogTagCount();
}
