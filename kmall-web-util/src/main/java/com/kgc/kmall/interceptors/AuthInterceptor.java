package com.kgc.kmall.interceptors;

import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断是否是HandlerMethod，因为访问静态资源时handler是ResourceHttpRequestHandler
        if (handler.getClass().equals(HandlerMethod.class)) {
            //获取注解信息
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
            // 没有LoginRequired注解不拦截
            if (methodAnnotation == null) {
//                System.out.println("没有添加注解不需要拦截");
                return true;
            }else{
                String token="";
                //cookie中存在token
                String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
                if(StringUtils.isNotBlank(oldToken)){
                    token = oldToken;
                }
                //参数中存在token
                String newToken = request.getParameter("token");
                if(StringUtils.isNotBlank(newToken)){
                    token = newToken;
                }

                //token验证
                String result="fail";

                //判断methodAnnotation的value值
                boolean value = methodAnnotation.value();
                if(value){
                    //必须登录,如果token无效返回false，跳转login
                    if(result.equals("success")==false){
                        return false;
                    }
                }
                //登录成功
                if(result.equals("success")){
                    request.setAttribute("memberId","1");
                    request.setAttribute("nickname","test");
                    //验证通过，覆盖cookie中的token
                    if(StringUtils.isNotBlank(token)){
                        CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
                    }
                    return true;
                }
            }
        }
        return true;
    }
}
