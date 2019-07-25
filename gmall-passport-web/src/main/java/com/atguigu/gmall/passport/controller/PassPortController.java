package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.passport.config.JwtUtil;
import com.atguigu.gmall.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassPortController {

    @Value("${token.key}")
    String signKey;
    @Reference
    UserService userService;



    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        //保存上
        request.setAttribute("originUrl",originUrl);
        return "index";
    }

    /*@Test
    public void test01(){
        String key = "atguigu";
        String ip="192.168.45.131";
        Map map = new HashMap();
        map.put("userId","1001");
        map.put("nickName","marry");
        String token = JwtUtil.encode(key, map, ip);
        Map<String, Object> decode = JwtUtil.decode(token, key, "192.168.45.131");
    }*/

    //登录
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UserInfo userInfo){
        //取得IP地址
        String remoteAddr = request.getHeader("X-forwarded-for");
        if(userInfo !=null){
            UserInfo loginUser = userService.login(userInfo);
            if(loginUser ==null){
                return "fail";
            }else{
                //生成token
                HashMap map = new HashMap();
                map.put("userId", loginUser.getId());
                map.put("nickName", loginUser.getNickName());
                String token = JwtUtil.encode(signKey, map, remoteAddr);
                return token;
            }
        }
        return "fail";
    }

    //验证登录
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        String token = request.getParameter("token");
        String currentIp = request.getParameter("currentIp");
        // 检查token
        // Map<String, Object> map = JwtUtil.decode(token, signKey, currentIp);
        Map<String, Object> map = JwtUtil.decode(token, signKey, currentIp);
        if (map!=null){
            // 检查redis信息
            String userId = (String) map.get("userId");
            UserInfo userInfo = userService.verify(userId);
            if (userInfo!=null){
                return "success";
            }
        }
        return "fail";
    }



}
