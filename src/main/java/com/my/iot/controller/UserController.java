package com.my.iot.controller;

import com.my.iot.domain.Result;
import com.my.iot.domain.User;
import com.my.iot.service.UserService;
import com.my.iot.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/regist", method = RequestMethod.POST)
    @ResponseBody
    public Result regist(User user) throws Exception {//注册
        //调用service完成注册
        user.setPassword(Md5Util.encodeByMd5(user.getPassword()));//对密码加密
        boolean flag = userService.regist(user);
        return new Result(flag, flag ? "regist success" : "regist failed", null);
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result login(User user, HttpSession session) throws Exception {//登录
        user.setPassword(Md5Util.encodeByMd5(user.getPassword()));//对密码加密
        //调用service完成登录
        User u = userService.login(user);
        if (u == null) {
            return new Result(false, "login failed", null);
        } else {
            //添加user到session中
            session.setAttribute("login_user", u);
            return new Result(true, "login success", u);
        }
    }

    @RequestMapping(path = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Result loginUserInfo(HttpSession session) {//查看登录用户信息
        User login_user = (User) session.getAttribute("login_user");
        if (login_user == null) {
            return new Result(false, "no login", null);
        }
        return new Result(true, "success", login_user);
    }

    @RequestMapping(path = "/exit", method = RequestMethod.GET)
    @ResponseBody
    public Result exit(HttpSession session) {//退出登录
        User login_user = (User) session.getAttribute("login_user");
        if (login_user == null) {
            return new Result(false, "exit failed, no login", null);
        }
        session.removeAttribute("login_user");
        session.invalidate();
        return new Result(true, "exit success", null);
    }

    //以下方法要经过拦截器的登录拦截
    @RequestMapping(path = "/password", method = RequestMethod.POST)
    @ResponseBody
    public Result changePassword(User user, String newPassword, HttpSession session) throws Exception {//修改密码
        //1.拿到登录用户
        User login_user = (User) session.getAttribute("login_user");
        //2.验证用户旧密码是否正确
        user.setPassword(Md5Util.encodeByMd5(user.getPassword()));//对密码加密
        if (userService.login(user) == null) {
            return new Result(false, "failed, old password error", null);
        }
        //3.更改新密码
        newPassword = Md5Util.encodeByMd5(newPassword);
        login_user.setPassword(newPassword);
        //调用service完成修改密码
        userService.update(login_user);
        //更新session
        session.setAttribute("login_user", login_user);
        return new Result(true, "success", null);
    }

    //以下方法要经过拦截器的登录拦截
    @RequestMapping(path = "/modify", method = RequestMethod.POST)
    @ResponseBody
    public Result changeBasicInformation(User user, HttpSession session) {//修改用户基本信息（用户名不能修改）
        //1.拿到登录用户
        User login_user = (User) session.getAttribute("login_user");
        //2.更新基本信息
        login_user.setEmail(user.getEmail());
        login_user.setNickname(user.getNickname());
        login_user.setTel(user.getTel());
        //调用service完成修改基本信息
        userService.update(login_user);
        //更新session
        session.setAttribute("login_user", login_user);
        return new Result(true, "success", login_user);
    }
}
