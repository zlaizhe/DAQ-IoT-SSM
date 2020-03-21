package com.my.iot.controller;

import com.my.iot.domain.*;
import com.my.iot.service.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/api/data")
public class DataController implements InitializingBean {

    @Autowired
    private DataService dataService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private GatewayExceptionService gatewayExceptionService;

    @Autowired
    private SensorExceptionService sensorExceptionService;

    //@Autowired
    //rivate RedisTemplate<String, Data> redisTemplate;

    //@RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result addData(@PathVariable("id") int sensor_id) {//该方法仅供测试，给一个传感器添加一些随机数据，不经缓存直接添加到数据库
        Sensor sensor = sensorService.findById(sensor_id);
        if (sensor == null) {
            return new Result(false, "sensor not exist", null);
        }
        List<Data> datas = new ArrayList<>();
        long now = System.currentTimeMillis();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            now += 1000;
            Data tmp = new Data(null, (float) (i * 0.2), new Date(now), sensor_id);
            System.out.print(tmp.getData());
            datas.add(tmp);
        }
        dataService.add(datas);

        return new Result(true, "add success", datas);
    }


    //只接受json格式的传感器提交数据，数据格式：{"id":null,"data":1.23,"time":"2020-01-01 13:13:13","sensor_id":1}
    @RequestMapping(path = "/receive", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Result receive(@RequestBody Data data) {//接受一个传感器数据，存入redis缓存，只返回提交状态，不响应数据
        System.out.println(data);
        Result result = checkData(data);
        if (!result.getStatus()) {//检查数据是否合法
            return result;
        }

        //将数据存入缓存
        dataService.add(data);
        //ListOperations<String, Data> listOperations = redisTemplate.opsForList();
        //listOperations.rightPush("addCache|datas", data);//存入datas的添加缓存
        return result;
    }

    //数据格式：[{"id":null,"data":1.23,"time":"2020-01-01 13:13:13","sensor_id":1}, ...]
    @RequestMapping(path = "/receiveAll", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Result receiveAll(@RequestBody List<Data> datas) {//接受多个个传感器数据，存入redis缓存，只返回提交状态，不响应数据
        System.out.println(datas);
        Result result = checkData(datas);
        if (!result.getStatus()) {//检查数据是否合法
            return result;
        }
        //将数据存入缓存
        dataService.add(datas);
        //ListOperations<String, Data> listOperations = redisTemplate.opsForList();
        //for (Data data : datas) {
        //    listOperations.rightPush("addCache|datas", data);//存入datas的添加缓存
        //}
        return result;
    }

    private Result checkData(Data data) {//保证提交数据除id外均为非空，且保证sensor_id存在
        if (data.getData() == null || data.getTime() == null || data.getSensor_id() == null) {
            return new Result(false, "data deficient", null);
        }
        if (data.getTime().after(new Date())) {//如果日期比现在还超前则不接受
            return new Result(false, "date illegal", null);
        }
        Integer sensor_id = data.getSensor_id();
        Sensor sensor = sensorService.findById(sensor_id);
        if (sensor == null) {
            return new Result(false, "sensor not exist", null);
        }
        //模拟产生传感器异常
        if (data.getData() < 0.1) {
            //this.createSensorException(sensor_id);
            System.out.println("出现传感器异常...");
        }
        //模拟产生网关异常
        if (data.getData() < 0.01) {
            Integer gate_id = sensor.getGate_id();
            //this.createGatewayException(gate_id);
            System.out.println("出现网关异常...");
        }

        return new Result(true, "receive finished", null);
    }

    private Result checkData(List<Data> datas) {//保证提交所有数据除id外均为非空，且保证所有sensor_id存在
        for (Data data : datas) {
            Result result = checkData(data);
            if (!result.getStatus()) {
                return result;
            }
        }
        return new Result(true, "receive finished", null);
    }

    public void createGatewayException(Integer gate_id) {//模拟创建网关异常
        String mesage[] = {"未接收到数据", "连接异常", "网络异常", "数据异常"};
        int index = new Random().nextInt(4);
        GatewayException gatewayException = new GatewayException(null, mesage[index], new Date(), gate_id);
        gatewayExceptionService.add(gatewayException);
    }

    private void createSensorException(Integer sensor_id) {//模拟创建传感器异常
        String mesage[] = {"未接收到数据", "连接异常", "网络异常", "数据异常"};
        int index = new Random().nextInt(4);
        SensorException sensorException = new SensorException(null, mesage[index], new Date(), sensor_id);
        sensorExceptionService.add(sensorException);
    }

    //该控制器的初始化方法
    @Override
    public void afterPropertiesSet() throws Exception {
        //清空缓存
        /*
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            redisTemplate.delete(key);
        }

        //查询数据库Data，按照传感器id存入缓存
        List<Data> datas = dataService.findAll();
        ListOperations<String, Data> listOperations = redisTemplate.opsForList();
        for (Data data : datas) {
            listOperations.rightPush("GetCache|datas|sensorId=" + data.getSensor_id(), data);//将数据库存储的所有输入存入查询缓存
        }
        */
        //System.out.println("DataController初始化完成");
    }

    @RequestMapping(value = "/sensor/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result getDataBySensorId(@PathVariable("id") int sensor_id) {//查询一个传感器下的所有数据
        Sensor sensor = sensorService.findById(sensor_id);
        if (sensor == null) {
            return new Result(false, "sensor not exist", null);
        }
        List<Data> datas = dataService.findBySensorId(sensor_id);
        //List<Data> datas = redisTemplate.opsForList().range("GetCache|datas|sensorId=" + sensor.getId(), 0, -1);//从缓存中查询
        if (datas == null || datas.isEmpty()) {
            return new Result(false, "the sensor has no data", null);
        }
        return new Result(true, "get success", datas);
    }
}
