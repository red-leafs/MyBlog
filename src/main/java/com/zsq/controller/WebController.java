package com.zsq.controller;

import com.zsq.common.CommonResult;
import com.zsq.common.ResultGenerator;
import com.zsq.controller.vo.BlogDetailVO;
import com.zsq.entity.Comment;
import com.zsq.service.BlogService;
import com.zsq.service.CommentService;
import com.zsq.service.TagService;
import com.zsq.util.BlogUtils;
import com.zsq.util.PageResult;
import com.zsq.util.PatternUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;


    //首页界面
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        return this.page(request,1);
    }

    @GetMapping({"/page/{pageNum}"})
    public String page(HttpServletRequest request, @PathVariable("pageNum") int pageNum) {
        PageResult blogPageResult = blogService.getBlogsForIndexPage(pageNum);
        if (blogPageResult == null) {
            return "error/error_404";
        }
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("newBlogs", blogService.getBlogListForIndexPage(1));
        request.setAttribute("hotBlogs", blogService.getBlogListForIndexPage(0));
        request.setAttribute("hotTags", tagService.getBlogTagCountForIndex());
        request.setAttribute("pageName", "首页");
        return "blog/index";
    }


    /**
     * 搜索列表页
     *
     * @return
     */
    @GetMapping({"/search/{keyword}"})
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword) {
        return search(request, keyword, 1);
    }
    /**
     * 搜索列表页
     *
     * @return
     */
    @GetMapping({"/search/{keyword}/{page}"})
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword, @PathVariable("page") Integer page) {
        PageResult blogPageResult = blogService.getBlogsPageBySearch(keyword, page);
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "搜索");
        request.setAttribute("pageUrl", "search");
        request.setAttribute("keyword", keyword);
        return "blog/list";
    }


    /**
     * 分类列表页
     *
     * @return
     */
    @GetMapping({"/category/{categoryName}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName) {
        return category(request, categoryName, 1);
    }
    /**
     * 分类列表页
     *
     * @return
     */
    @GetMapping({"/category/{categoryName}/{page}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName, @PathVariable("page") Integer page) {
        PageResult blogPageResult = blogService.getBlogsPageByCategory(categoryName, page);
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "分类");
        request.setAttribute("pageUrl", "category");
        request.setAttribute("keyword", categoryName);
        return "blog/list";
    }

    /**
     * 标签列表页
     *
     * @return
     */
    @GetMapping({"/tag/{tagName}"})
    public String tag(HttpServletRequest request, @PathVariable("tagName") String tagName) {
        return tag(request, tagName, 1);
    }
    /**
     * 标签列表页
     *
     * @return
     */
    @GetMapping({"/tag/{tagName}/{page}"})
    public String tag(HttpServletRequest request, @PathVariable("tagName") String tagName, @PathVariable("page") Integer page) {
        PageResult blogPageResult = blogService.getBlogsPageByTag(tagName, page);
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "标签");
        request.setAttribute("pageUrl", "tag");
        request.setAttribute("keyword", tagName);
        return "blog/list";
    }

    /**
     * 详情页
     *
     * @return
     */
    @GetMapping("/blog/{blogId}")
    public String detail(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
        BlogDetailVO blogDetailVO = blogService.getBlogDetail(blogId);
        if (blogDetailVO != null) {
            request.setAttribute("blogDetailVO", blogDetailVO);
        }
        request.setAttribute("pageName", "详情");
        return "blog/detail";
    }


    /**
     * 博客评论操作
     */
    @PostMapping(value = "/blog/comment")
    @ResponseBody
    public CommonResult comment(HttpServletRequest request, HttpSession session, @RequestParam Long blogId, @RequestParam String verifyCode, @RequestParam String commentator, @RequestParam String email, @RequestParam String websiteUrl, @RequestParam String commentBody) {
        if (StringUtils.isEmpty(verifyCode)) {
            return ResultGenerator.fail("验证码不能为空");
        }
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if (StringUtils.isEmpty(kaptchaCode)) {
            return ResultGenerator.fail("非法请求");
        }
        if (!verifyCode.equals(kaptchaCode)) {
            return ResultGenerator.fail("验证码错误");
        }
        String ref = request.getHeader("Referer");
        if (StringUtils.isEmpty(ref)) {
            return ResultGenerator.fail("非法请求");
        }
        if (null == blogId || blogId < 0) {
            return ResultGenerator.fail("非法请求");
        }
        if (StringUtils.isEmpty(commentator)) {
            return ResultGenerator.fail("请输入称呼");
        }
        if (StringUtils.isEmpty(email)) {
            return ResultGenerator.fail("请输入邮箱地址");
        }
        if (!PatternUtil.isEmail(email)) {
            return ResultGenerator.fail("请输入正确的邮箱地址");
        }
        if (StringUtils.isEmpty(commentBody)) {
            return ResultGenerator.fail("请输入评论内容");
        }
        if (commentBody.trim().length() > 200) {
            return ResultGenerator.fail("评论内容过长");
        }
        Comment comment = new Comment();
        comment.setBlogId(blogId);
        comment.setCommentator(BlogUtils.cleanString(commentator));
        comment.setEmail(email);
        if (PatternUtil.isURL(websiteUrl)) {
            comment.setWebsiteUrl(websiteUrl);
        }
        comment.setCommentBody(BlogUtils.cleanString(commentBody));
        return ResultGenerator.success(commentService.addComment(comment));
    }
}
