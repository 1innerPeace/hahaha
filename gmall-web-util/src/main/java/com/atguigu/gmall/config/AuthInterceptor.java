package com.atguigu.gmall.config;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.atguigu.gmall.util.HttpClientUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

//配置拦截器
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    //拦截得到token
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("newToken");
        //把token保存到cookie
        if(token!=null){
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
        }
        if(token==null){
            token = CookieUtil.getCookieValue(request, "token", false);
        }

        if(token!=null) {
            //读取token
            Map map = getUserMapByToken(token);
            String nickName = (String) map.get("nickName");
            request.setAttribute("nickName", nickName);
        }
        // 知道方法上是否有注解@LoginRequire
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取请求的方法，并获取方法上的注解
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (methodAnnotation!=null){
            // 开始准备认证 verify ();  在passport-web 中 web-util 访问 passport-web 跨域 ： @CrossOrigin， httpclient，jsonp
            // 获取salt
            String salt = request.getHeader("X-forwarded-for");
            // httpclient 远程调用 doget，dopost

            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + salt);


            if ("success".equals(result)){
                // 保存用户Id
                Map map = getUserMapByToken(token);
                // 通过key nickName 获取用户昵称
                String userId = (String) map.get("userId");
                //  保存作用域
                request.setAttribute("userId",userId);
                // 用户登录进行放行！
                return true;
            }else {
                // 还需要看一下当前注解中的属性autoRedirect
                if (methodAnnotation.autoRedirect()){
                    // 跳转登录页面！
                    // http://passport.atguigu.com/index?originUrl=http%3A%2F%2Fitem.gmall.com%2F39.html
                    String requestURL  = request.getRequestURL().toString(); // http://item.gmall.com/39.html
                    // base64进行编码
                    String encodeURL = URLEncoder.encode(requestURL, "UTF-8"); // http%3A%2F%2Fitem.gmall.com%2F39.html
                    // 页面跳转
                    response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
                    //返回false重新验证
                    return false;
                }
            }
        }

        return true;
    }
    //将token解码得到json串
    private  Map getUserMapByToken(String  token){
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] tokenBytes = base64UrlCodec.decode(tokenUserInfo);
        String tokenJson = null;
        try {
            tokenJson = new String(tokenBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map map = JSON.parseObject(tokenJson, Map.class);
        return map;
    }
}
