package com.zsq.mapper;

import com.zsq.entity.Category;
import com.zsq.util.PageQueryUtil;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectCategoryList(PageQueryUtil pageQueryUtil);

    int selectTotalCategories();

    Category selectByCategoryName(String categoryName);

    boolean deleteById(Integer[] id);

    List<Category> selectAllCategories();

    List<Category> selectByCategoryIds(List<Integer> categoryIds);
}