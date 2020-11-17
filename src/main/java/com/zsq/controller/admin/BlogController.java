package com.zsq.controller.admin;

import com.zsq.common.CommonResult;
import com.zsq.common.ResultGenerator;
import com.zsq.entity.Blog;
import com.zsq.service.BlogService;
import com.zsq.service.CategoryService;
import com.zsq.util.BlogUtils;
import com.zsq.util.PageQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

//博客路由
@Controller
@RequestMapping("/admin")
public class BlogController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private CategoryService categoryService;

    //博客页面跳转
    @GetMapping("/blogs")
    public String blogPage(HttpServletRequest request) {
        request.setAttribute("path", "blogs");
        return "admin/blog";
    }

    //用于接收图片上传请求并返回图片路径给 Editor.md 编辑器
    @PostMapping("/blogs/md/uploadfile")
    public void uploadFileByEditormd(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(name = "editormd-image-file", required = true)
                                             MultipartFile file) throws IOException, URISyntaxException {
        String FILE_UPLOAD_DIC = "/home/project/upload/";//上传文件的默认url前缀，根据部署设置自行修改
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //生成文件名称通用方法
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();
        //创建文件
        File destFile = new File(FILE_UPLOAD_DIC + newFileName);
        String fileUrl = BlogUtils.getHost(new URI(request.getRequestURL() + "")) + "/upload/" + newFileName;
        File fileDirectory = new File(FILE_UPLOAD_DIC);
        try {
            if (!fileDirectory.exists()) {
                if (!fileDirectory.mkdir()) {
                    throw new IOException("文件夹创建失败,路径为：" + fileDirectory);
                }
            }
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            request.setCharacterEncoding("utf-8");
            response.setHeader("Content-Type", "text/html");
            response.getWriter().write("{\"success\": 1, \"message\":\"success\",\"url\":\"" + fileUrl + "\"}");
        } catch (UnsupportedEncodingException e) {
            response.getWriter().write("{\"success\":0}");
        } catch (IOException e) {
            response.getWriter().write("{\"success\":0}");
        }
    }

    //分页查询博客数据
    @GetMapping("/blogs/list")
    @ResponseBody
    public CommonResult list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.fail("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.success(blogService.getBlogsPage(pageUtil));
    }

    /**
     * 新建博客
     * @param blogTitle 文章标题
     * @param blogSubUrl 自定义路径
     * @param blogCategoryId 分类 id (下拉框中选择)
     * @param blogTags 标签字段(以逗号分隔)
     * @param blogContent 文章内容(编辑器中的 md 文档)
     * @param blogCoverImage 封面图(上传图片或者随机图片的路径)
     * @param blogStatus 文章状态
     * @param enableComment 评论开关
     * @return
     */
    @PostMapping("/blogs/save")
    @ResponseBody
    public CommonResult save(@RequestParam("blogTitle") String blogTitle,
                             @RequestParam("blogSubUrl") String blogSubUrl,
                             @RequestParam("blogCategoryId") Integer blogCategoryId,
                             @RequestParam("blogTags") String blogTags,
                             @RequestParam("blogContent") String blogContent,
                             @RequestParam("blogCoverImage") String blogCoverImage,
                             @RequestParam("blogStatus") Byte blogStatus,
                             @RequestParam("enableComment") Byte enableComment){

        if (StringUtils.isEmpty(blogTitle)) {
            return ResultGenerator.fail("请输入文章标题");
        }
        if (blogTitle.trim().length() > 150) {
            return ResultGenerator.fail("标题过长");
        }
        if (StringUtils.isEmpty(blogTags)) {
            return ResultGenerator.fail("请输入文章标签");
        }
        if (blogTags.trim().length() > 150) {
            return ResultGenerator.fail("标签过长");
        }
        if (blogSubUrl.trim().length() > 150) {
            return ResultGenerator.fail("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)) {
            return ResultGenerator.fail("请输入文章内容");
        }
        if (blogTags.trim().length() > 100000) {
            return ResultGenerator.fail("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)) {
            return ResultGenerator.fail("封面图不能为空");
        }

        Blog blog = new Blog();
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String blogResult = blogService.saveBlog(blog);

        if ("success".equals(blogResult)){
            return ResultGenerator.success(blogResult);
        }
        return ResultGenerator.fail(blogResult);
    }

    //博客编辑页面
    @GetMapping("/blogs/edit/{blogId}")
    public String edit(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
        request.setAttribute("path", "edit");
        Blog blog = blogService.getBlogById(blogId);
        if (blog == null) {
            return "error/error_400";
        }
        request.setAttribute("blog", blog);
        request.setAttribute("categories", categoryService.selectAllCategories());
        return "admin/edit";
    }

    /**
     * 博客修改按钮
     * @param blogId 博客主键
     * @param blogTitle 博客标题
     * @param blogSubUrl 自定义路径
     * @param blogCategoryId 分类id(下拉框中选择)
     * @param blogTags 标签字段(以逗号分隔)
     * @param blogContent 博客内容(编辑器中的md文档)
     * @param blogCoverImage 封面图
     * @param blogStatus 博客状态
     * @param enableComment 评论开关
     * @return
     */
    @PostMapping("/blogs/update")
    @ResponseBody
    public CommonResult update(@RequestParam("blogId") Long blogId,
                         @RequestParam("blogTitle") String blogTitle,
                         @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
                         @RequestParam("blogCategoryId") Integer blogCategoryId,
                         @RequestParam("blogTags") String blogTags,
                         @RequestParam("blogContent") String blogContent,
                         @RequestParam("blogCoverImage") String blogCoverImage,
                         @RequestParam("blogStatus") Byte blogStatus,
                         @RequestParam("enableComment") Byte enableComment) {
        if (StringUtils.isEmpty(blogTitle)) {
            return ResultGenerator.fail("请输入文章标题");
        }
        if (blogTitle.trim().length() > 150) {
            return ResultGenerator.fail("标题过长");
        }
        if (StringUtils.isEmpty(blogTags)) {
            return ResultGenerator.fail("请输入文章标签");
        }
        if (blogTags.trim().length() > 150) {
            return ResultGenerator.fail("标签过长");
        }
        if (blogSubUrl.trim().length() > 150) {
            return ResultGenerator.fail("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)) {
            return ResultGenerator.fail("请输入文章内容");
        }
        if (blogTags.trim().length() > 100000) {
            return ResultGenerator.fail("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)) {
            return ResultGenerator.fail("封面图不能为空");
        }
        Blog blog = new Blog();
        blog.setBlogId(blogId);
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String updateBlogResult = blogService.updateBlog(blog);
        if ("success".equals(updateBlogResult)) {
            return ResultGenerator.success("修改成功");
        } else {
            return ResultGenerator.fail(updateBlogResult);
        }
    }


    //删除博客
    @PostMapping("/blogs/delete")
    @ResponseBody
    public CommonResult delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.fail("参数异常！");
        }
        if (blogService.deleteBatch(ids)) {
            return ResultGenerator.success();
        } else {
            return ResultGenerator.fail("删除失败");
        }
    }


}
