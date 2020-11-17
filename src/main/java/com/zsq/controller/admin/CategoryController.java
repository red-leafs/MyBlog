package com.zsq.controller.admin;

import com.zsq.common.CommonResult;
import com.zsq.common.ResultGenerator;
import com.zsq.service.CategoryService;
import com.zsq.util.PageQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class CategoryController {
    //分类列表分页接口
    //添加分类接口
    //根据 id 获取单条分类记录接口
    //修改分类接口
    //删除分类接口

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/categories")
    public String categoryPage(HttpServletRequest request) {
        request.setAttribute("path", "categories");
        return "admin/category";
    }

    //分类列表分页接口
    @RequestMapping("/categories/list")
    @ResponseBody
    public CommonResult list(@RequestParam Map<String ,Object> params) {
        //获取参数page当前页码和limit每页条数
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.fail("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        //获取分页数据
        return ResultGenerator.success(categoryService.getCategoryPage(pageUtil));
    }

    //添加分类接口
    @PostMapping("/categories/save")
    @ResponseBody
    public CommonResult save(@RequestParam("categoryName") String categoryName,
                             @RequestParam("categoryIcon") String categoryIcon){
        if (StringUtils.isEmpty(categoryName)) {
            return ResultGenerator.fail("请输入分类名称！");
        }
        if (StringUtils.isEmpty(categoryIcon)) {
            return ResultGenerator.fail("请选择分类图标！");
        }
        if (categoryService.insertCategory(categoryName, categoryIcon)) {
            return ResultGenerator.success();
        } else {
            return ResultGenerator.fail("分类名称已存在");
        }
    }

    //删除分类接口
    @PostMapping("/categories/delete")
    @ResponseBody
    public CommonResult delete(@RequestBody Integer[] id){
        if (id.length < 1){
            return ResultGenerator.fail("参数异常");
        }
        if (categoryService.deleteCategory(id)){
            return ResultGenerator.success();
        }
        return ResultGenerator.fail("删除失败");
    }


}
