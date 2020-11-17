package com.zsq.service;

import com.zsq.entity.Category;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;

import java.util.List;

public interface CategoryService {

    PageResult getCategoryPage(PageQueryUtil pageUtil);

    boolean insertCategory(String categoryName, String categoryIcon);

    boolean deleteCategory(Integer[] id);

    List<Category> selectAllCategories();

}
