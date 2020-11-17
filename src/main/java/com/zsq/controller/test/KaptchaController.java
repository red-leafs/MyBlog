package com.zsq.controller.test;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Controller
public class KaptchaController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    //生成图片验证码
    @RequestMapping("/kaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse) throws Exception{

        //用字节数组存储验证码对象
        byte[] captchaOutputStream = null;
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();

        try {
            //生产验证码字符串并保存到session中
            String verifyCode = captchaProducer.createText();
            httpServletRequest.getSession().setAttribute("verifyCode", verifyCode);

            //生成图片格式的验证码
            BufferedImage challenge = captchaProducer.createImage(verifyCode);
            //写入图片格式的验证码
            ImageIO.write(challenge, "jpg", imgOutputStream);
        } catch (IllegalArgumentException e) {
            //返回404错误代码
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        //字节流转换为一个byte数组
        captchaOutputStream = imgOutputStream.toByteArray();
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();

        responseOutputStream.write(captchaOutputStream);

        responseOutputStream.flush();
        responseOutputStream.close();
    }

    //验证码比对
    @ResponseBody
    @RequestMapping("/verify")
    public String verify(String verifyCode,
                         HttpSession session){
        if (StringUtils.isEmpty(verifyCode)) {
            return "验证码不能为空";
        }
        //获取session的验证码
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        //验证码比对
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            return "验证码错误";
        }
        return "验证成功";
    }


}
