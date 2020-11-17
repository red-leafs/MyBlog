package com.zsq.mapper;

import com.zsq.entity.TagRelation;

import java.util.List;

public interface TagRelationMapper {
    int deleteByPrimaryKey(Long relationId);

    int insert(TagRelation record);

    int insertSelective(TagRelation record);

    TagRelation selectByPrimaryKey(Long relationId);

    int updateByPrimaryKeySelective(TagRelation record);

    int updateByPrimaryKey(TagRelation record);

    List<Long> selectByIds(Integer[] id);

    int batchInsert(List<TagRelation> blogTagRelations);

    int deleteByBlogId(Long blogId);
}