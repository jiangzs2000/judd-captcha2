package com.shuyuan.judd.captcha.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ICaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.shuyuan.judd.base.cache.CacheService;
import spring.shuyuan.judd.base.model.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/captcha")
@Api(tags = "图文验证码相关操作")
public class CaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);
    @Autowired
    private CacheService cacheService;
    /**
     * 生成图像验证码
     * @param response response请求对象
     * @throws IOException
     */
    @ApiOperation(value="生成图像验证码")
    @GetMapping("/get")
    public void generateValidateCode(@RequestParam(name="token", required = true) String token, HttpServletResponse response) throws Exception {
        //设置response响应
        // response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        //定义图形验证码的长和宽
        ICaptcha captcha = CaptchaUtil.createShearCaptcha(126, 30,4,2);
        //ICaptcha captcha = CaptchaUtil.createLineCaptcha(126, 30,4,15);

        logger.debug("captcha code is {}", captcha.getCode());
        //把凭证对应的验证码信息保存到reids（可从redis中获取）
        cacheService.putString(token, captcha.getCode(), 600); //10 分钟有效
        logger.debug("the captcha for token '{}' is '{}'", token,captcha.getCode());
        //输出浏览器
        OutputStream out=response.getOutputStream();
        captcha.write(out);
        out.flush();
        out.close();
    }

    @PostMapping("/verify")
    @ApiOperation(value = "验证token对应的captcha")
    public Response<String> verifyCaptcha(@RequestParam(name="token") String token, @RequestParam(name="code") String code) throws Exception {
        if(StringUtils.isBlank(code)){
            return Response.createNativeFail("验证码不能为空");
        }
        String captcha = cacheService.getString(token);
        logger.debug("captcha gotten for token '{}' is {}, inputed capathc is {}",token,captcha,code);
        if(code.equalsIgnoreCase(captcha)){
            logger.debug("matched");
            return Response.createSuccess("验证通过");
        }else{
            logger.debug("unmatch");
            return Response.createNativeFail("验证码错误");
        }
    }
}
