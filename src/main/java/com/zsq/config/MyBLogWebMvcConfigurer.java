package com.zsq.config;

import com.zsq.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyBLogWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private UserInterceptor userInterceptor;

    //注册拦截器
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


    //请求映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:/home/project/upload/");
    }
}
