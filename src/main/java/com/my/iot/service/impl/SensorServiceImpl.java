package com.my.iot.service.impl;

import com.my.iot.domain.Sensor;
import com.my.iot.mapper.DataMapper;
import com.my.iot.mapper.SensorMapper;
import com.my.iot.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("SensorService")
public class SensorServiceImpl implements SensorService {
    @Autowired
    private SensorMapper sensorMapper;

    @Autowired
    private DataMapper dataMapper;

    @Override
    public void add(Sensor sensor) {
        sensorMapper.add(sensor);
    }

    @Override
    public Sensor findById(int id) {
        return sensorMapper.findById(id);
    }

    @Override
    public List<Sensor> findAll() {
        return sensorMapper.findAll();
    }

    @Override
    public void update(Sensor sensor) {
        sensorMapper.update(sensor);
    }

    @Override
    public void deleteById(int id) {
        //先删除传感器下的所有数据
        Sensor sensor = sensorMapper.findById(id);
        dataMapper.deleteBySensorId(sensor.getId());
        //再删除传感器
        sensorMapper.deleteById(id);
    }

    @Override
    public List<Sensor> findByClassifyId(int classify_id) {
        return sensorMapper.findByClassifyId(classify_id);
    }

    @Override
    public List<Sensor> findByGatewayId(int gateway_id) {
        return sensorMapper.findByGatewayId(gateway_id);
    }

    @Override
    public Sensor findByIdWithDatas(int id) {
        return sensorMapper.findByIdWithDatas(id);
    }
}
