package com.my.iot.service.impl;

import com.my.iot.domain.Sensor;
import com.my.iot.domain.SensorClassify;
import com.my.iot.mapper.SensorClassifyMapper;
import com.my.iot.mapper.SensorMapper;
import com.my.iot.service.SensorClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sensorClassifyService")
public class SensorClassifyServiceImpl implements SensorClassifyService {

    @Autowired
    private SensorClassifyMapper sensorClassifyMapper;

    @Autowired
    private SensorMapper sensorMapper;

    @Override
    public void add(SensorClassify classify) {
        sensorClassifyMapper.add(classify);
    }

    @Override
    public List<SensorClassify> findAll(Boolean withSensors) {
        List<SensorClassify> classifies = sensorClassifyMapper.findAll();//查询所有分类
        if (new Boolean(true).equals(withSensors)) {
            for (SensorClassify classify : classifies) {//查询并封装每个分类下的传感器
                Integer classifyId = classify.getId();
                List<Sensor> sensors = sensorMapper.findByClassifyId(classifyId);
                classify.setSensors(sensors);
            }
        }
        return classifies;
    }

    @Override
    public SensorClassify findById(int id) {
        return sensorClassifyMapper.findById(id);
    }

    @Override
    public List<SensorClassify> findByGatewayId(int gateway_id) {
        return sensorClassifyMapper.findByGatewayId(gateway_id);
    }
}
