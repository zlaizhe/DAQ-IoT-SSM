package com.my.iot.util;

import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义的字符串转日期的转换器
 * 需要在springmvc.xml中配置
 */
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        if (source == null) {
            throw new RuntimeException("需要传入数据！");
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException("数据类型转换出错！");
        }
    }
}
