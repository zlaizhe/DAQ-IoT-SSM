package com.my.iot.service.impl;

import com.my.iot.domain.Gateway;
import com.my.iot.domain.Sensor;
import com.my.iot.mapper.DataMapper;
import com.my.iot.mapper.GatewayMapper;
import com.my.iot.mapper.SensorMapper;
import com.my.iot.service.GatewayService;
import com.my.iot.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("gatewayService")
public class GatewayServiceImpl implements GatewayService {
    @Autowired
    private GatewayMapper gatewayMapper;

    @Autowired
    private SensorMapper sensorMapper;

    @Autowired
    private DataMapper dataMapper;


    @Override
    public void add(Gateway gateway) {
        gatewayMapper.insert(gateway);
    }

    @Override
    public Gateway findById(int id) {
        return gatewayMapper.findById(id);
    }

    @Override
    public void update(Gateway gateway) {
        gatewayMapper.update(gateway);
    }

    @Override
    public void deleteById(int id) {
        //先删除网关下的传感器及其所有数据
        List<Sensor> sensors = sensorMapper.findByGatewayId(id);
        for (Sensor sensor : sensors) {
            Integer sensorId = sensor.getId();
            dataMapper.deleteBySensorId(sensorId);
            sensorMapper.deleteById(sensorId);
        }
        //再删除网关
        gatewayMapper.deleteById(id);
    }

    @Override
    public List<Gateway> findAll(Boolean withSensors) {
        List<Gateway> gateways = gatewayMapper.findAll();//查询所有网关信息
        if (new Boolean(true).equals(withSensors)) {
            for (Gateway gateway : gateways) {//查询并封装网关的传感器信息
                Integer gatewayId = gateway.getId();
                List<Sensor> sensors = sensorMapper.findByGatewayId(gatewayId);
                gateway.setSensors(sensors);
            }
        }
        return gateways;
    }

    @Override
    public Gateway findByIdWithSensors(int id) {
        return gatewayMapper.findByIdWithSensors(id);
    }
}
