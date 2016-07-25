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
import com.jinyufeili.minas.wechat.data.AqiLevel;
import com.jinyufeili.minas.wechat.util.AqiUtils;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author pw
 */
@Service
public class WeatherMessageHandler extends AbstractTextResponseMessageHandler {

    public static final int ALLOWED_LAG = 10 * 60 * 1000;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final String[] HOME_USERS = {"obQ_WwJFoQCEw7RqINOxd7Y_59zI", "obQ_WwBgW__vZwHvHXF_CdW5sIEM"};

    @Autowired
    private DataPointService dataPointService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        DataPoint temperature = dataPointService.query(DataPointType.TEMPERATURE, 1).get(0);
        DataPoint humidity = dataPointService.query(DataPointType.HUMIDITY, 1).get(0);
        DataPoint pm25 = dataPointService.query(DataPointType.PM25, 1).get(0);
        DataPoint pressure = dataPointService.query(DataPointType.PRESSURE, 1).get(0);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年M月d日 H点mm分");
        List<String> messages = new ArrayList<>();
        messages.add(String.format("%s", formatter.format(new Date(temperature.getTimestamp()))));
        messages.add(String.format("温度: %.1f℃", temperature.getValue()));
        messages.add(String.format("湿度: %.1f%%", humidity.getValue()));
        messages.add(String.format("气压: %.0fhPa", pressure.getValue() / 100.0));

        if (System.currentTimeMillis() - pm25.getTimestamp() < ALLOWED_LAG) {
            double average =
                    dataPointService.query(DataPointType.PM25, 10).stream().mapToDouble(DataPoint::getValue).average()
                            .getAsDouble();

            messages.add(String.format("PM2.5: %.0fug/m^3", average));

            AqiLevel usAqi = AqiUtils.getAqi(AqiLevel.US_AQI_LEVELS, average);
            if (usAqi == null) {
                messages.add("空气污染指数(美标): 超限");
            } else {
                messages.add(String.format("空气指数(美标): %.0f - %s", AqiUtils.calcAqiValue(usAqi, average),
                        usAqi.getName()));
            }

            AqiLevel cnAqi = AqiUtils.getAqi(AqiLevel.CN_AQI_LEVELS, average);
            if (usAqi == null) {
                messages.add("空气污染指数(国标): 超限");
            } else {
                messages.add(String.format("空气指数(国标): %.0f - %s", AqiUtils.calcAqiValue(cnAqi, average),
                        cnAqi.getName()));
                messages.add(cnAqi.getDesc());
            }
        }

        if (ArrayUtils.indexOf(HOME_USERS, message.getFromUserName()) != -1) {
            DataPoint temperatureHome = dataPointService.query(DataPointType.TEMPERATURE_HOME, 1).get(0);
            DataPoint humidityHome = dataPointService.query(DataPointType.HUMIDITY_HOME, 1).get(0);
            DataPoint pm25Home = dataPointService.query(DataPointType.PM25_HOME, 1).get(0);
            messages.add(String.format("\n家里温度: %.1f℃", temperatureHome.getValue()));
            messages.add(String.format("家里湿度: %.1f%%", humidityHome.getValue()));
            if (System.currentTimeMillis() - pm25Home.getTimestamp() < ALLOWED_LAG) {
                messages.add(String.format("家里PM2.5: %.0fug/m^3", pm25Home.getValue()));
            }
        }

        messages.add("\n<a href=\"http://air.jinyufeili.com\">小区24小时空气质量</a>");
        messages.add("\n--------\n注: \n* 温湿度为实时数据\n* PM2.5 数据为10分钟滑动平均值\n* 采样点位于铂庭二区");

        return String.join("\n", messages);
    }
}
