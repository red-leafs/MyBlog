MyBlog项目
	统一错误代码Generator


	登录功能
		根据用户名和密码查询记录
		MD5Util对密码加密
			//加密字符串
			String passwordMd5 = MD5Util.MD5Encode(password, "UTF-8");
			return userMapper.login(username,passwordMd5);

		设置超时登录过期
			//session过期时间设置为7200秒 即两小时
			session.setMaxInactiveInterval(60 * 60 * 2);

		验证码功能verifyCode
			验证码的生成(kaptcha框架)
			验证码的显示
			验证码的比对

		拦截非法登录功能
			拦截器配置类//实现WebMvcConfigurer
				@Configuration
				public class MyBLogWebMvcConfigurer implements WebMvcConfigurer {

				    @Autowired
				    private UserInterceptor userInterceptor;

				    @Override
				    public void addInterceptors(InterceptorRegistry registry) {
				        //拦截的请求地址
				        String[] addPathPatterns = {
				                "/admin/**"
				        };

				        //要排除的路径，不拦截
				        String[] excludePathPatterns = {
				                "/admin/login",
				                "/admin/dist/**",
				                "/admin/plugins/**"
				        };
				        registry.addInterceptor(userInterceptor).addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
				    }
				}

			拦截器对象//实现HandlerInterceptor
				@Component
				public class UserInterceptor implements HandlerInterceptor{

				    @Override
				    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
				        String uri = request.getRequestURI();
				        if (uri.startsWith("/admin") && null == request.getSession().getAttribute("id")) {
				            //验证失败，返回登录界面
				            request.getSession().setAttribute("error", "请登陆");
				            response.sendRedirect(request.getContextPath() + "/admin/login");
				            return false;
				        } else {
				            //验证通过，清除错误信息
				            request.getSession().removeAttribute("error");
				            return true;
				        }
				    }
				}


	博客的分类功能
		分类列表分页接口
		添加分类接口
		根据 id 获取单条分类记录接口
		修改分类接口
		删除分类接口 //批量删除使用@RequestBody ?

		分页功能实现
			list        列表数据
			totalCount  总记录数
			pageSize    每页记录数
			currPage    当前页数
			totalPage	总页数

	标签模块 //标签与博客的关系为多对多，需建立关系表
		列表分页接口
		添加标签接口
		删除标签接口

	文章模块
		文章表设计
			USE `my_blog_db`;

			DROP TABLE IF EXISTS `tb_blog`;

			CREATE TABLE `tb_blog` (
			  `blog_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '博客表主键id',
			  `blog_title` varchar(200) NOT NULL COMMENT '博客标题',
			  `blog_sub_url` varchar(200) NOT NULL COMMENT '博客自定义路径url',
			  `blog_cover_image` varchar(200) NOT NULL COMMENT '博客封面图',
			  `blog_content` mediumtext NOT NULL COMMENT '博客内容',
			  `blog_category_id` int(11) NOT NULL COMMENT '博客分类id',
			  `blog_category_name` varchar(50) NOT NULL COMMENT '博客分类(冗余字段)',
			  `blog_tags` varchar(200) NOT NULL COMMENT '博客标签',
			  `blog_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-草稿 1-发布',
			  `blog_views` bigint(20) NOT NULL DEFAULT '0' COMMENT '阅读量',
			  `enable_comment` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0-允许评论 1-不允许评论',
			  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
			  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
			  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
			  PRIMARY KEY (`blog_id`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8;

		文章添加
			文章标签数量不得大于6个-->分类表判断增加-->标签表判断增加-->博客标签关系表增加-->完成增加

		文章编辑
		文章查询
		文章删除


	搜索功能
		模糊查询
			<!--查询分页数据,page当前页和limit每页条数-->
			<select id="findBlogList" parameterType="Map" resultMap="BaseResultMap">
			  select
			  <include refid="Base_Column_List"/>
			  from tb_blog
			  where is_deleted=0
			  <if test="keyword!=null">
			    AND (blog_title like CONCAT('%','${keyword}','%' ) or blog_category_name like CONCAT('%','${keyword}','%' ))
			  </if>
			  <if test="blogStatus!=null">
			    AND blog_status = #{blogStatus}
			  </if>
			  order by blog_id desc
			  <if test="start!=null and limit!=null">
			    limit #{start},#{limit}
			  </if>
			</select>

			<!--获取总记录数-->
			<select id="getTotalBlogs" parameterType="Map" resultType="int">
			  select count(*) from tb_blog
			  where is_deleted=0
			  <if test="keyword!=null">
			    AND (blog_title like CONCAT('%','${keyword}','%' ) or blog_category_name like CONCAT('%','${keyword}','%' ))
			  </if>
			  <if test="blogStatus!=null">
			    AND blog_status = #{blogStatus}
			  </if>
			</select>

	友情链接

	文章评论

	错误页面

	其余功能
		代码高亮
		返回顶部
		markdown目录生成



实用的方法
	//通过","的分隔符拆分成字符串数组/split
	String[] tags = blog.getBlogTags().split(",")
	//集合批量增加集合/addAll
	allTagsList.addAll(tagListForInsert)
	//集合非空方法/isEmpty
	CollectionUtils.isEmpty(list)
	//复制对象属性方法/copyProperties
	BeanUtils.copyProperties(对象, 本体)
	//获取blog集合中的分类id集合方法
   List<Integer> categoryIds = blogList.stream().map(Blog::getBlogCategoryId).collect(Collectors.toList());



存在问题

	登录错误后，session未重置，验证码错误问题怎么解决

	首页里设置这么多属性的作用和原理

	为什么分页方法要传入一个Map集合的params而不是QueryPage对象

	@RequestBody + 参数
		添加 @ResponseBody 注解后，Spring Boot 会直接将对象转换为 json 格式并输出为响应信息，这是将对象作为相应数据的例子。 接下来我们再写一个案例，使用 @RequestBody 接收前端请求并将参数转换为后端定义的对象，在 TestController 类中添加的方法如下，请求方法为 POST，并使用 @RequestBody 注解将前端传输的参数直接转换为 SaleGoods 对象。
		前端 ajax 传输的数据是 5 个字段，到达后端后直接转换为 SaleGoods 对象。由于消息转换器的存在，对象数据的读取不仅简单而且完全正确，响应时也不用自行封装工具类，使得开发过程变得更加灵活和高效。

	如何使用帮助文档，使用一些使用的工具类和方法


技术构成
	Spring Boot 静态资源
	Spring Boot 开发 web 项目
	Spring Boot 整合 Thymeleaf 流程
	Spring Boot 文件上传
	Spring Boot 整合 MyBatis
	Spring Boot 事务处理
	RESTful api 设计规范
	AdminLTE3 模板整合
	自定义错误页面
	Editormd 编辑器介绍及整合
	Spring Boot 消息转换器
	Thymeleaf 讲解
	Thymeleaf 语法详解
	Spring Boot 连接 MySQL 流程
	Mybatis-Generator 自动生成代码
	Ajax 调用接口实践
	SpringBoot 整合 kaptcha 验证码生成
	JqGrid 分页组件整合
	Spring Boot 中使用拦截器
	jar 包和 war 包部署
