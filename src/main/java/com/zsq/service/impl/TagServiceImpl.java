package com.zsq.service.impl;

import com.zsq.entity.BlogTagCount;
import com.zsq.entity.Tag;
import com.zsq.mapper.BlogTagMapper;
import com.zsq.mapper.TagMapper;
import com.zsq.mapper.TagRelationMapper;
import com.zsq.service.TagService;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagRelationMapper tagRelationMapper;
    @Autowired
    private BlogTagMapper blogTagMapper;

    //标签列表分页接口
    @Override
    public PageResult getTagPage(PageQueryUtil pageQueryUtil) {
        List<Tag> tagList = tagMapper.selectTagList(pageQueryUtil);
        int total = tagMapper.selectTotalTags();

        PageResult pageResult = new PageResult(tagList,total,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    //添加标签
    @Override
    public boolean insertTag(String tagName) {
        Tag temp = tagMapper.selectByTagName(tagName);
        if (temp == null) {
            Tag blogTag = new Tag();
            blogTag.setTagName(tagName);
            return tagMapper.insertSelective(blogTag) > 0;
        }
        return false;
    }

    //删除标签
    @Override
    public boolean delete(Integer[] ids) {
        //查询是否有关联关系，已存在关联关系不删除
        List<Long> relations = tagRelationMapper.selectByIds(ids);
        if (!CollectionUtils.isEmpty(relations)) {
            return false;
        }
        return tagMapper.deleteById(ids);
    }

    //首页标签显示
    public List<BlogTagCount> getBlogTagCountForIndex() {
        return blogTagMapper.getBlogTagCount();
    }

}
