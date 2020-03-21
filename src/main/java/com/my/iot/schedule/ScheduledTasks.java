package com.my.iot.schedule;

import com.my.iot.domain.Data;
import com.my.iot.service.DataService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

//@Component
public class ScheduledTasks implements Runnable, InitializingBean {//定时任务，暂未启用

    @Autowired
    private RedisTemplate<String, Data> redisTemplate;

    @Autowired
    private DataService dataService;

    @Override
    public void run() {
        try {
            while (true) {
                //定时将redis中添加的缓存数据存入数据库
                Data data;
                while ((data = redisTemplate.opsForList().leftPop("addCache|datas")) != null) {
                    dataService.add(data);
                }
                Thread.sleep(300 * 1000);//每5分钟执行一次
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //随着IOC容器的创建，启动线程，开始定时任务
        new Thread(this).start();
        System.out.println("定时任务已启动...");
    }
}
