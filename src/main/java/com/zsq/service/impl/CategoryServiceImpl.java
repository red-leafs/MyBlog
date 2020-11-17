package com.zsq.service.impl;

import com.zsq.entity.Category;
import com.zsq.mapper.CategoryMapper;
import com.zsq.service.CategoryService;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 分类列表分页
     * @param pageUtil page当前页码和limit每页条数
     * @return 需要提供page当前页码
     *                limit每页条数
     *                list当页数据
     *                total总记录数
     */
    @Override
    public PageResult getCategoryPage(PageQueryUtil pageUtil) {
        List<Category> categoryList = categoryMapper.selectCategoryList(pageUtil);
        int total = categoryMapper.selectTotalCategories();
        PageResult pageResult = new PageResult(categoryList,total,pageUtil.getPage(),pageUtil.getLimit());
        return pageResult;
    }


    /**
     * 添加分类
     * @param categoryName 分类名称
     * @param categoryIcon 分类标志
     * @return
     */
    @Override
    public boolean insertCategory(String categoryName, String categoryIcon) {
        Category temp = categoryMapper.selectByCategoryName(categoryName);
        if (temp == null) {
            Category Category = new Category();
            Category.setCategoryName(categoryName);
            Category.setCategoryIcon(categoryIcon);
            return categoryMapper.insertSelective(Category) > 0;
        }
        return false;
    }

    //批量删除分类
    @Override
    public boolean deleteCategory(Integer[] id) {
        if(id.length < 1){
            return false;
        }
        return categoryMapper.deleteById(id);
    }

    //查询所有分类
    @Override
    public List<Category> selectAllCategories() {
        return categoryMapper.selectAllCategories();
    }

}
