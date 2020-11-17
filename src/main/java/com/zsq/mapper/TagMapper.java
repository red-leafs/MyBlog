package com.zsq.mapper;

import com.zsq.entity.Tag;
import com.zsq.util.PageQueryUtil;

import java.util.List;

public interface TagMapper {
    int deleteByPrimaryKey(Integer tagId);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByPrimaryKey(Integer tagId);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);

    List<Tag> selectTagList(PageQueryUtil pageQueryUtil);

    int selectTotalTags();

    Tag selectByTagName(String tagName);

    boolean deleteById(Integer[] ids);

    int batchInsertBlogTag(List tagList);
}