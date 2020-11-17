package com.zsq.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserInterceptor implements HandlerInterceptor{

    //违规登录拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin") && null == request.getSession().getAttribute("userId")) {
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
