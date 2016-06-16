/**
 * @(#)${FILE_NAME}.java, 6/16/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.sensor.data.DataPoint;
import com.jinyufeili.minas.sensor.data.DataPointType;
import com.jinyufeili.minas.sensor.service.DataPointService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pw
 */
@Service
public class WeatherMessageHandler extends AbstractTextResponseMessageHandler {

    public static final int ALLOWED_LAG = 10 * 60 * 1000;

    @Autowired
    private DataPointService dataPointService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        DataPoint temperature = dataPointService.query(DataPointType.BMP_TEMPERATURE, 1).get(0);
        DataPoint humidity = dataPointService.query(DataPointType.HUMIDITY, 1).get(0);
        DataPoint pm25 = dataPointService.query(DataPointType.PM25, 1).get(0);

        List<String> messages = new ArrayList<>();
        if (System.currentTimeMillis() - temperature.getTimestamp() < ALLOWED_LAG) {
            messages.add(String.format("温度: %.1f摄氏度", temperature.getValue()));
        }

        if (System.currentTimeMillis() - humidity.getTimestamp() < ALLOWED_LAG) {
            messages.add(String.format("湿度: %.1f%%", humidity.getValue()));
        }

        if (System.currentTimeMillis() - pm25.getTimestamp() < ALLOWED_LAG) {
            messages.add(String.format("PM2.5: %.0fug/m^3", pm25.getValue()));
        }

        messages.add(String.format("湿度: %.1f", temperature.getValue()));
        String.format("温度: %.1f\n湿度: %.1f", temperature.getValue(), humidity.getValue());
        return null;
    }
}
