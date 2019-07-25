package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.passport.config.JwtUtil;
import com.atguigu.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    @Value("${token.key}")
    String signKey;
    @Reference
    private UserService userService;



    @RequestMapping("findAll")
    @ResponseBody
    public List<UserInfo> findAll(){
        return userService.getUserInfoListAll();
    }


   /* @RequestMapping("verify")
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
    }*/

}
