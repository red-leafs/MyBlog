package com.zsq.controller.admin;

import com.zsq.common.CommonResult;
import com.zsq.common.ResultGenerator;
import com.zsq.service.TagService;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    //tag页面
    @GetMapping("/tags")
    public String tagPage(HttpServletRequest request) {
        request.setAttribute("path", "tags");
        return "admin/tag";
    }

    //标签列表分页接口
    @RequestMapping("/tags/list")
    @ResponseBody
    public CommonResult list(Map<String,Object> params){
        //获取参数page,limit
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.fail("参数异常！");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult pageResult = tagService.getTagPage(pageQueryUtil);

        return ResultGenerator.success(pageResult);
    }

    //添加分类接口
    @PostMapping("/tags/save")
    @ResponseBody
    public CommonResult save(@RequestParam("tagName") String tagName){
        if (StringUtils.isEmpty(tagName)) {
            return ResultGenerator.fail("请输入分类名称！");
        }
        if (tagService.insertTag(tagName)) {
            return ResultGenerator.success();
        } else {
            return ResultGenerator.fail("分类名称已存在");
        }
    }

    //删除分类接口
    @PostMapping("/tags/delete")
    @ResponseBody
    public CommonResult delete(@RequestBody Integer[] ids){
        if (ids.length < 1){
            return ResultGenerator.fail("参数异常");
        }
        if (tagService.delete(ids)){
            return ResultGenerator.success();
        }
        return ResultGenerator.fail("删除失败");
    }

}
