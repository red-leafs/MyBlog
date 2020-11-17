package com.zsq.service.impl;

import com.zsq.controller.vo.BlogDetailVO;
import com.zsq.controller.vo.BlogListVO;
import com.zsq.controller.vo.SimpleBlogListVO;
import com.zsq.entity.Blog;
import com.zsq.entity.Category;
import com.zsq.entity.Tag;
import com.zsq.entity.TagRelation;
import com.zsq.mapper.BlogMapper;
import com.zsq.mapper.CategoryMapper;
import com.zsq.mapper.TagMapper;
import com.zsq.mapper.TagRelationMapper;
import com.zsq.service.BlogService;
import com.zsq.util.MarkDownUtil;
import com.zsq.util.PageQueryUtil;
import com.zsq.util.PageResult;
import com.zsq.util.PatternUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagRelationMapper tagRelationMapper;

    //新建博客
    @Override
    @Transactional //开启事务
    public String saveBlog(Blog blog) {

        //判断分类数据
        Category category = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
        if(category == null){
            blog.setBlogCategoryId(0);
            blog.setBlogCategoryName("默认分类");
        }else{
            blog.setBlogCategoryName(category.getCategoryName());
            //分类的排序值加1
            category.setCategoryRank(category.getCategoryRank() + 1);
        }

        //处理标签数据
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length > 6) {
            return "标签数量限制为6";
        }

        //保存文章
        if (blogMapper.insertSelective(blog) > 0){
            //新增的tag对象
            List<Tag> tagListForInsert = new ArrayList<>();
            //所有的tag对象，用于建立关系数据
            List<Tag> allTagsList = new ArrayList<>();
            for (int i = 0; i < tags.length; i++) {
                Tag tag = tagMapper.selectByTagName(tags[i]);
                if (tag == null) {
                    //不存在就新增
                    Tag tempTag = new Tag();
                    tempTag.setTagName(tags[i]);
                    tagListForInsert.add(tempTag);
                } else {
                    allTagsList.add(tag);
                }
            }

            //新增标签数据并修改分类排序值
            if (!CollectionUtils.isEmpty(tagListForInsert)) {
                tagMapper.batchInsertBlogTag(tagListForInsert);
            }
            categoryMapper.updateByPrimaryKeySelective(category);
            List<TagRelation> blogTagRelations = new ArrayList<>();
            //新增关系数据
            allTagsList.addAll(tagListForInsert);
            for (Tag tag : allTagsList) {
                TagRelation blogTagRelation = new TagRelation();
                blogTagRelation.setBlogId(blog.getBlogId());
                blogTagRelation.setTagId(tag.getTagId());
                blogTagRelations.add(blogTagRelation);
            }
            if (tagRelationMapper.batchInsert(blogTagRelations) > 0) {
                return "success";
            }
        }
        return "添加失败";
    }


    //通过id搜索博客
    @Override
    public Blog getBlogById(Long id) {
        return blogMapper.selectByPrimaryKey(id);
    }


    //更新博客
    @Override
    @Transactional
    public String updateBlog(Blog blog) {
        Blog blogForUpdate = blogMapper.selectByPrimaryKey(blog.getBlogId());
        if (blogForUpdate == null) {
            return "数据不存在";
        }
        blogForUpdate.setBlogTitle(blog.getBlogTitle());
        blogForUpdate.setBlogSubUrl(blog.getBlogSubUrl());
        blogForUpdate.setBlogContent(blog.getBlogContent());
        blogForUpdate.setBlogCoverImage(blog.getBlogCoverImage());
        blogForUpdate.setBlogStatus(blog.getBlogStatus());
        blogForUpdate.setEnableComment(blog.getEnableComment());
        Category blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
        if (blogCategory == null) {
            blogForUpdate.setBlogCategoryId(0);
            blogForUpdate.setBlogCategoryName("默认分类");
        } else {
            //设置博客分类名称
            blogForUpdate.setBlogCategoryName(blogCategory.getCategoryName());
            blogForUpdate.setBlogCategoryId(blogCategory.getCategoryId());
            //分类的排序值加1
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
        }
        //处理标签数据
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length > 6) {
            return "标签数量限制为6";
        }
        blogForUpdate.setBlogTags(blog.getBlogTags());
        //新增的tag对象
        List<Tag> tagListForInsert = new ArrayList<>();
        //所有的tag对象，用于建立关系数据
        List<Tag> allTagsList = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            Tag tag = tagMapper.selectByTagName(tags[i]);
            if (tag == null) {
                //不存在就新增
                Tag tempTag = new Tag();
                tempTag.setTagName(tags[i]);
                tagListForInsert.add(tempTag);
            } else {
                allTagsList.add(tag);
            }
        }
        //新增标签数据不为空->新增标签数据
        if (!CollectionUtils.isEmpty(tagListForInsert)) {
            tagMapper.batchInsertBlogTag(tagListForInsert);
        }
        List<TagRelation> blogTagRelations = new ArrayList<>();
        //新增关系数据
        allTagsList.addAll(tagListForInsert);
        for (Tag tag : allTagsList) {
            TagRelation blogTagRelation = new TagRelation();
            blogTagRelation.setBlogId(blog.getBlogId());
            blogTagRelation.setTagId(tag.getTagId());
            blogTagRelations.add(blogTagRelation);
        }
        //修改blog信息->修改分类排序值->删除原关系数据->保存新的关系数据
        categoryMapper.updateByPrimaryKeySelective(blogCategory);
        //删除原关系数据
        tagRelationMapper.deleteByBlogId(blog.getBlogId());
        tagRelationMapper.batchInsert(blogTagRelations);
        if (blogMapper.updateByPrimaryKeySelective(blogForUpdate) > 0) {
            return "success";
        }
        return "修改失败";
    }

    /**
     * 分类列表分页
     * @param pageUtil page当前页码和limit每页条数
     * @return 需要提供page当前页码
     *                limit每页条数
     *                list当页数据
     *                total总记录数
     */
    public PageResult getBlogsPage(PageQueryUtil pageUtil) {
        List<Blog> blogList = blogMapper.findBlogList(pageUtil);
        int total = blogMapper.getTotalBlogs(pageUtil);
        PageResult pageResult = new PageResult(blogList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }


    //删除博客
    public boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        return blogMapper.deleteBatch(ids) > 0;
    }


    /**
     * 获取首页侧边栏数据列表
     * @param type 0-点击最多 1-最新发布
     * @return
     */
    public List<SimpleBlogListVO> getBlogListForIndexPage(int type) {
        List<SimpleBlogListVO> simpleBlogListVOS = new ArrayList<>();
        List<Blog> blogs = blogMapper.findBlogListByType(type, 9);
        if (!CollectionUtils.isEmpty(blogs)) {
            for (Blog blog : blogs) {
                SimpleBlogListVO simpleBlogListVO = new SimpleBlogListVO();
                BeanUtils.copyProperties(blog, simpleBlogListVO);
                simpleBlogListVOS.add(simpleBlogListVO);
            }
        }
        return simpleBlogListVOS;
    }


    /**
     * 获取首页文章列表
     * @param page 页码
     * @return
     */
    public PageResult getBlogsForIndexPage(int page) {
        Map params = new HashMap();
        params.put("page", page);
        //每页8条
        params.put("limit", 8);
        params.put("blogStatus", 1);//过滤发布状态下的数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        List<Blog> blogList = blogMapper.findBlogList(pageUtil);
        List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
        int total = blogMapper.getTotalBlogs(pageUtil);
        PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    /**
     * 数据填充方法 集合Blog --> BlogListVO
     */
    private List<BlogListVO> getBlogListVOsByBlogs(List<Blog> blogList) {
        List<BlogListVO> blogListVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(blogList)) {
            //获取blog集合中的分类id集合
            List<Integer> categoryIds = blogList.stream().map(Blog::getBlogCategoryId).collect(Collectors.toList());
            Map<Integer, String> blogCategoryMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(categoryIds)) {
                List<Category> blogCategories = categoryMapper.selectByCategoryIds(categoryIds);
                if (!CollectionUtils.isEmpty(blogCategories)) {
                    blogCategoryMap = blogCategories.stream().collect(Collectors.toMap(Category::getCategoryId, Category::getCategoryIcon, (key1, key2) -> key2));
                }
            }
            for (Blog blog : blogList) {
                BlogListVO blogListVO = new BlogListVO();
                BeanUtils.copyProperties(blog, blogListVO);
                if (blogCategoryMap.containsKey(blog.getBlogCategoryId())) {
                    blogListVO.setBlogCategoryIcon(blogCategoryMap.get(blog.getBlogCategoryId()));
                } else {
                    blogListVO.setBlogCategoryId(0);
                    blogListVO.setBlogCategoryName("默认分类");
                    blogListVO.setBlogCategoryIcon("/admin/dist/img/category/1.png");
                }
                blogListVOS.add(blogListVO);
            }
        }
        return blogListVOS;
    }


    /**
     * 根据搜索关键字获取首页文章列表
     *
     * @param page
     * @return
     */
    public PageResult getBlogsPageBySearch(String keyword, Integer page) {
        if (page > 0 && PatternUtil.validKeyword(keyword)) {
            Map param = new HashMap();
            param.put("page", page);
            param.put("limit", 9);
            param.put("keyword", keyword);
            param.put("blogStatus", 1);//过滤发布状态下的数据
            PageQueryUtil pageUtil = new PageQueryUtil(param);
            List<Blog> blogList = blogMapper.findBlogList(pageUtil);
            List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
            int total = blogMapper.getTotalBlogs(pageUtil);
            PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
            return pageResult;
        }
        return null;
    }

    /**
     * 根据分类获取首页文章列表
     *
     * @param page
     * @return
     */
    public PageResult getBlogsPageByCategory(String categoryName, Integer page) {
        if (PatternUtil.validKeyword(categoryName)) {
            Category blogCategory = categoryMapper.selectByCategoryName(categoryName);
            if ("默认分类".equals(categoryName) && blogCategory == null) {
                blogCategory = new Category();
                blogCategory.setCategoryId(0);
            }
            if (blogCategory != null && page > 0) {
                Map param = new HashMap();
                param.put("page", page);
                param.put("limit", 9);
                param.put("blogCategoryId", blogCategory.getCategoryId());
                param.put("blogStatus", 1);//过滤发布状态下的数据
                PageQueryUtil pageUtil = new PageQueryUtil(param);
                List<Blog> blogList = blogMapper.findBlogList(pageUtil);
                List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
                int total = blogMapper.getTotalBlogs(pageUtil);
                PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
                return pageResult;
            }
        }
        return null;
    }

    /**
     * 根据标签获取首页文章列表
     *
     * @param page
     * @return
     */
    public PageResult getBlogsPageByTag(String tagName, Integer page) {
        if (PatternUtil.validKeyword(tagName)) {
            Tag tag = tagMapper.selectByTagName(tagName);
            if (tag != null && page > 0) {
                Map param = new HashMap();
                param.put("page", page);
                param.put("limit", 9);
                param.put("tagId", tag.getTagId());
                PageQueryUtil pageUtil = new PageQueryUtil(param);
                List<Blog> blogList = blogMapper.getBlogsPageByTagId(pageUtil);
                List<BlogListVO> blogListVOS = getBlogListVOsByBlogs(blogList);
                int total = blogMapper.getTotalBlogsByTagId(pageUtil);
                PageResult pageResult = new PageResult(blogListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
                return pageResult;
            }
        }
        return null;
    }

    /**
     * 文章详情获取
     *
     * @param blogId
     * @return
     */
    public BlogDetailVO getBlogDetail(Long blogId) {
        Blog blog = blogMapper.selectByPrimaryKey(blogId);
        //不为空且状态为已发布
        BlogDetailVO blogDetailVO = getBlogDetailVO(blog);
        if (blogDetailVO != null) {
            return blogDetailVO;
        }
        return null;
    }

    /**
     * 方法抽取
     *
     * @param blog
     * @return
     */
    private BlogDetailVO getBlogDetailVO(Blog blog) {
        //判空以及发布状态是否为已发布
        if (blog != null && blog.getBlogStatus() == 1) {
            //增加浏览量
            blog.setBlogViews(blog.getBlogViews() + 1);
            blogMapper.updateByPrimaryKey(blog);
            BlogDetailVO blogDetailVO = new BlogDetailVO();
            BeanUtils.copyProperties(blog, blogDetailVO);
            //md格式转换，将 markdown 格式的 content 内容字段转换为带有 html 标签的页面，因为在后台管理系统中操作时使用的是 Editor.md 编辑器，存储到数据库中的字段也是 markdown 格式的字段，在页面中显示的话需要进行转换
            blogDetailVO.setBlogContent(MarkDownUtil.mdToHtml(blogDetailVO.getBlogContent()));
            Category blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
            if (blogCategory == null) {
                blogCategory = new Category();
                blogCategory.setCategoryId(0);
                blogCategory.setCategoryName("默认分类");
                blogCategory.setCategoryIcon("/admin/dist/img/category/00.png");
            }
            //分类信息
            blogDetailVO.setBlogCategoryIcon(blogCategory.getCategoryIcon());
            if (!StringUtils.isEmpty(blog.getBlogTags())) {
                //标签设置
                List<String> tags = Arrays.asList(blog.getBlogTags().split(","));
                blogDetailVO.setBlogTags(tags);
            }
            return blogDetailVO;
        }
        return null;
    }

}
