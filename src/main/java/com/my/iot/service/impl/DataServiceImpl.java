package com.my.iot.service.impl;

import com.my.iot.domain.Data;
import com.my.iot.mapper.DataMapper;
import com.my.iot.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dataService")
public class DataServiceImpl implements DataService {

    @Autowired
    private DataMapper dataMapper;

    @Override
    public void add(Data data) {
        dataMapper.add(data);
    }

    @Override
    public List<Data> findAll() {
        return dataMapper.findAll();
    }

    @Override
    public List<Data> findBySensorId(int sensor_id) {
        return dataMapper.findBySensorId(sensor_id);
    }

    @Override
    public void add(List<Data> datas) {
        for (Data data : datas) {
            dataMapper.add(data);
        }
    }
}
