package com.my.iot.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.iot.domain.Result;
import com.my.iot.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//拦截所有未登录的请求
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //System.out.println("拦截器...preHandle...");
        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("/login") || requestURI.endsWith("/home")) {//不拦截登录的controller，和Home的conroller
            return true;
        }

        HttpSession session = request.getSession();
        User login_user = (User) session.getAttribute("login_user");
        if (login_user == null) {
            //未登录，不放行
            response.setContentType("text/json;charset=UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            Result result = new Result(false, "please login", null);
            objectMapper.writeValue(response.getOutputStream(), result);
            return false;
        }

        return true;//登陆后放行
    }

}
