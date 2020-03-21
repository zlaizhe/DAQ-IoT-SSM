package com.my.iot.controller;

import com.my.iot.domain.GatewayException;
import com.my.iot.domain.PageBean;
import com.my.iot.domain.Result;
import com.my.iot.service.GatewayExceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/api/gatewayException")
public class GatewayExceptionController {

    @Autowired
    private GatewayExceptionService gatewayExceptionService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseBody
    public Result getAllGatewayException() {
        List<GatewayException> gatewayExceptions = gatewayExceptionService.findAll();
        return new Result(true, "get success", gatewayExceptions);
    }

    @RequestMapping(path = "/page/{page}", method = RequestMethod.GET)
    @ResponseBody
    public Result getAllGatewayExceptionByPage(@PathVariable("page") int page) {
        PageBean<GatewayException> pageBean = gatewayExceptionService.findByPage(page);
        return new Result(true, "get success", pageBean);
    }

    @RequestMapping(path = "/{timetamp}", method = RequestMethod.GET)
    @ResponseBody
    public Result getGatewayExceptionByTime(@PathVariable("timetamp") String timetamp) {//查询一段时间的异常，参数格式：时间戳1@时间戳2
        String[] s = timetamp.split("@");
        long dateFromTamp = Long.parseLong(s[0]);
        long dateToTamp = Long.parseLong(s[1]);
        List<GatewayException> gatewayExceptions = gatewayExceptionService.findByTime(new Date(dateFromTamp), new Date(dateToTamp));
        return new Result(true, "get success", gatewayExceptions);
    }
}
