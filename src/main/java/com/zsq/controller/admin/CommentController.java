package com.zsq.controller.admin;

import com.zsq.common.CommonResult;
import com.zsq.common.ResultGenerator;
import com.zsq.service.CommentService;
import com.zsq.util.PageQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/comments")
    public String list(HttpServletRequest request) {
        request.setAttribute("path", "comments");
        return "admin/comment";
    }

    /**
     * 评论列表
     */
    @GetMapping("/comments/list")
    @ResponseBody
    public CommonResult list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.fail("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.success(commentService.getCommentsPage(pageUtil));
    }

    //评论审核
    @PostMapping("/comments/checkDone")
    @ResponseBody
    public CommonResult checkDone(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.fail("参数异常！");
        }
        if (commentService.checkDone(ids)) {
            return ResultGenerator.success();
        } else {
            return ResultGenerator.fail("审核失败");
        }
    }

    //评论回复
    @PostMapping("/comments/reply")
    @ResponseBody
    public CommonResult checkDone(@RequestParam("commentId") Long commentId,
                            @RequestParam("replyBody") String replyBody) {
        if (commentId == null || commentId < 1 || StringUtils.isEmpty(replyBody)) {
            return ResultGenerator.fail("参数异常！");
        }
        if (commentService.reply(commentId, replyBody)) {
            return ResultGenerator.success();
        } else {
            return ResultGenerator.fail("回复失败");
        }
    }

    //评论删除
    @PostMapping("/comments/delete")
    @ResponseBody
    public CommonResult delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.fail("参数异常！");
        }
        if (commentService.deleteBatch(ids)) {
            return ResultGenerator.success();
        } else {
            return ResultGenerator.fail("刪除失败");
        }
    }
}