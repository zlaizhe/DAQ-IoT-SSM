package com.my.iot.controller;

import com.my.iot.domain.Gateway;
import com.my.iot.domain.Result;
import com.my.iot.domain.SensorClassify;
import com.my.iot.service.GatewayService;
import com.my.iot.service.SensorClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/classify")
public class SensorClassifyController {//该控制器依然采用restful风格，对传感器分类只提供增加和查询api

    @Autowired
    private SensorClassifyService sensorClassifyService;

    @Autowired
    private GatewayService gatewayService;

    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseBody
    public Result addClassify(SensorClassify classify) {
        if (classify.getName() == null || classify.getName().isEmpty()) {
            return new Result(false, "name can not be empty", null);
        }
        sensorClassifyService.add(classify);
        return new Result(true, "add success", classify);
    }

    @RequestMapping(path = "", method = RequestMethod.GET)
    @ResponseBody
    public Result getAllClassify(Boolean withSensors) {//请求不带这个布尔参数就不关联查询传感器
        List<SensorClassify> list = sensorClassifyService.findAll(withSensors);
        if (list == null || list.size() == 0) {
            return new Result(false, "no classify exist", null);
        }
        return new Result(true, "get success", list);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result getClassifyById(@PathVariable("id") int id) {
        SensorClassify classify = sensorClassifyService.findById(id);
        if (classify == null) {
            return new Result(false, "classify not exist", null);
        }
        return new Result(true, "get success", classify);
    }

    @RequestMapping(path = "/gateway/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result getClassifyByGatewayId(@PathVariable("id") int gateway_id) {//获取某一网关下的所有传感器分类（需要使用多对多查询）
        Gateway gateway = gatewayService.findById(gateway_id);
        if (gateway == null) {// 网关存在
            return new Result(false, "gateway not exist", null);
        }
        List<SensorClassify> classifys = sensorClassifyService.findByGatewayId(gateway_id);
        if (classifys == null || classifys.isEmpty()) {
            return new Result(false, "classify not exist", null);
        }
        return new Result(true, "get success", classifys);
    }
}
