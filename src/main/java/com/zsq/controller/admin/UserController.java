package com.zsq.controller.admin;

import com.zsq.entity.User;
import com.zsq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    UserService userService;

    //登录界面
    @RequestMapping("/login")
    public String login() {
        return "admin/login";
    }

    //首页界面
    @GetMapping({"", "/", "/index", "/index.html"})
    public String index() {
        return "admin/index";
    }

    //登录验证
    @PostMapping("/login")
    public String login(@RequestParam("userName") String username,
                        String password,
                        String verifyCode,
                        HttpSession session){


        //若用户名或密码为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            session.setAttribute("error","用户名或密码不能为空");
            return "admin/login";
        }

        //验证验证码
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            session.setAttribute("error", "验证码错误");
            return "admin/login";
        }

        //验证账号密码
        User user = userService.login(username,password);
        //验证成功
        if (user != null){
            //获得用户的ID和昵称
            session.setAttribute("userId",user.getAdminUserId());
            session.setAttribute("nickName",user.getNickName());
            //session过期时间(秒)
            session.setMaxInactiveInterval(60 * 60 * 2);
            return "redirect:/admin/index";
        }
        //验证失败
        session.setAttribute("error","登录失败，请检查用户名或密码");
        return "admin/login";
    }



    //简介界面
    @RequestMapping("/profile")
    public String profile(HttpServletRequest request){
        Integer id = (int) request.getSession().getAttribute("userId");
        User user = userService.getUserById(id);
        if (user == null) {
            return "admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName",user.getLoginUserName());
        request.setAttribute("nickName", user.getNickName());
        return "admin/profile";
    }

    //修改姓名
    @PostMapping("/profile/name")
    @ResponseBody
    public String updateName(HttpServletRequest request,
                                 String loginUserName,
                                 String nickName){

        //若姓名为空
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)){
            return "姓名不能为空";
        }
        Integer id = (Integer) request.getSession().getAttribute("userId");
        User user = userService.getUserById(id);

        if (userService.updateName(id,loginUserName,nickName)){
            request.getSession().removeAttribute("userId");
            request.getSession().removeAttribute("nickName");
            request.getSession().removeAttribute("error");
            return "success";
        }
        return "fail";
    }

    //修改密码
    @PostMapping("/profile/password")
    @ResponseBody
    public String updatePassword(HttpServletRequest request,
                                 String originalPassword,
                                 String newPassword){

        //若密码为空
        if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)){
            return "密码不能为空";
        }
        Integer id = (Integer) request.getSession().getAttribute("userId");
        User user = userService.getUserById(id);

        //修改密码，返回登录
        if (userService.updatePassword(id,originalPassword,newPassword)){
            request.getSession().removeAttribute("userId");
            request.getSession().removeAttribute("nickName");
            request.getSession().removeAttribute("error");
            return "success";
        }
        return "fail";
    }

    //登出验证
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("userId");
        request.getSession().removeAttribute("nickName");
        request.getSession().removeAttribute("error");
        return "admin/login";
    }

}
